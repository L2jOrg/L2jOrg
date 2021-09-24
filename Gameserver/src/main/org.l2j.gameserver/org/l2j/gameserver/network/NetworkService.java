/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network;

import org.l2j.commons.util.FileUtil;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.network.auth.AuthService;
import org.l2j.gameserver.network.auth.PacketHandler;
import org.l2j.gameserver.network.auth.SendablePacket;
import org.l2j.gameserver.network.provider.multi.MultiNetworkProvider;
import org.l2j.gameserver.network.provider.single.SingleNetworkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author JoeAlisson
 */
public class NetworkService extends XmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkService.class);

    private final List<Network> networks = new ArrayList<>();

    private String ipServiceDiscovery;
    private String providerType;
    private NetworkServiceProvider provider;

    private final Collection<AuthService> authServers = new ArrayList<>();

    private NetworkService() {
        // singleton
    }

    @Override
    protected Path getSchemaFilePath() {
        return FileUtil.resolvePath("config/xsd/networking.xsd");
    }

    public void load() {
        parseFile(FileUtil.resolveFilePath("config/networking.xml"));
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        var networksNode = doc.getFirstChild();
        this.ipServiceDiscovery = parseString(networksNode.getAttributes(), "ip-service-discovery");
        this.providerType = parseString(networksNode.getAttributes(), "provider-type");


        for(var node = networksNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            parseNetwork(node);
        }
    }

    private void parseNetwork(Node node) {
        var subnets = parseNetworkAddress(node.getFirstChild());

        var attr = node.getAttributes();
        var port = parseShort(attr, "port");
        var key = parseString(attr, "key");
        var authServerHost = parseString(attr, "auth-server-host");
        var authServerPort = parseShort(attr, "auth-server-port");
        var authServerKey = parseString(attr, "auth-server-key");

        var network = new Network(key, port, authServerHost, authServerPort, authServerKey, subnets);

        if(networks.add(network)) {
            LOGGER.info("add new network on port {} to auth server {}:{}",  port, authServerHost, authServerPort);
        }
    }

    private Collection<Subnet> parseNetworkAddress(Node node) {
        return switch (node.getNodeName()) {
            case "auto-discovery" -> autoDiscovery();
            case "subnet" -> parseSubnets(node);
            default -> Collections.emptyList();
        };
    }

    private Collection<Subnet> parseSubnets(Node node) {
        Collection<Subnet> subnets = new ArrayList<>();
        while (node != null) {
            var attr = node.getAttributes();
            var address = parseString(attr, "address");
            var host = parseString(attr, "host");

            if("#external".equals(host)) {
                host = internetAddress();
            }

            subnets.add(new Subnet(address, host));

            node = node.getNextSibling();
        }
        return subnets;
    }

    private Collection<Subnet> autoDiscovery() {
        Collection<Subnet> subnets = new HashSet<>();
        try {
            subnetsFromNetworkInterfaces(subnets);
        } catch (SocketException e) {
            LOGGER.warn("Could not config network automatically", e);
        }

        subnets.add(new Subnet("0.0.0.0/0", internetAddress()));
        return subnets;
    }

    private void subnetsFromNetworkInterfaces(Collection<Subnet> subnets) throws SocketException {
        var interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            var network = interfaces.nextElement();
            if (!isValid(network)) continue;

            parseNetworkInterface(subnets, network);
        }
    }

    private String internetAddress() {
        var host = "127.0.0.1";
        try {
            var url = new URL(ipServiceDiscovery);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                host = in.readLine();
            }
        } catch (Exception e) {
            LOGGER.warn("could not resolve the internet address. Using the loopback address 127.0.0.1", e);
        }
        return host;
    }

    private void parseNetworkInterface(Collection<Subnet> subnets, NetworkInterface network) {
        for (var interfaceAddress : network.getInterfaceAddresses()) {
            if(interfaceAddress.getAddress() instanceof Inet6Address) {
                continue;
            }
            var subnet = parseSubnet(interfaceAddress);
            subnets.add(subnet);
            LOGGER.info("Add new subnet {}", subnet);
        }
    }

    private Subnet parseSubnet(InterfaceAddress interfaceAddress) {
        var host = interfaceAddress.getAddress().getHostAddress();
        var address = calcSubnetAddress(host, interfaceAddress);
        return new Subnet(address, host);
    }

    private String calcSubnetAddress(String hostAddress, InterfaceAddress ia) {
        var mask = IntStream.rangeClosed(1, ia.getNetworkPrefixLength()).reduce((r, e) ->  (r << 1) + 1).orElse(0) << (32 - ia.getNetworkPrefixLength());
        var host = Arrays.stream(hostAddress.split("\\.")).mapToInt(Integer::parseInt).reduce((r, e) -> (r << 8) + e).orElse(0);
        var subnet = host & mask;
        return String.format("%d.%d.%d.%d/%d", (subnet >> 24) & 0xFF, (subnet >> 16) & 0xFF, (subnet >> 8 ) & 0xFF, subnet & 0xFF, ia.getNetworkPrefixLength());
    }

    private boolean isValid(NetworkInterface network) throws SocketException {
        if(!network.isUp() || network.isVirtual()) {
            return false;
        }
        return network.isLoopback() || (network.getHardwareAddress() != null && network.getHardwareAddress().length == 6);
    }

    public void shutdown() {
        provider.shutdown();
    }

    public void closeAuthConnection() {
        provider.closeAuthConnection();
    }

    public void sendPacketToAuth(SendablePacket packet) {
        provider.sendPacketToAuth(-1, packet);
    }

    public void sendPacketToAuth(int authKey, SendablePacket packet) {
        provider.sendPacketToAuth(authKey, packet);
    }

    public GameClient removeAuthedClient(int authKey, String account) {
        return provider.removeAuthedClient(authKey, account);
    }

    public GameClient removeWaitingClient(int authKey, String account) {
        return provider.removeWaitingClient(authKey, account);
    }

    public GameClient getAuthedClient(int authKey, String account) {
        return provider.getAuthedClient(authKey, account);
    }

    public GameClient addAuthedClient(int authKey, GameClient client) {
        return provider.addAuthedClient(authKey, client);
    }

    public GameClient addWaitingClient(int authKey, GameClient client) {
        return provider.addWaitingClient(authKey, client);
    }

    public void sendChangePassword(String accountName, String curpass, String newpass) {
        for (AuthService authServer : authServers) {
            authServer.sendChangePassword(accountName, curpass, newpass);
        }
    }

    public static void init() throws IOException {
        var instance = getInstance();
        instance.load();
        if(instance.networks.isEmpty()) {
            throw new IllegalStateException("There is no Networking defined. Check the networking.xml file");
        }
        instance.provider = switch (instance.providerType) {
            case "single-networking" -> new SingleNetworkProvider();
            case "multi-networking" -> new MultiNetworkProvider();
            default -> ServiceLoader.load(NetworkServiceProvider.class).findFirst().orElse(null);
        };

        if(instance.provider == null) {
            throw new IllegalStateException("There is no Network Service Provider. check the networking.xml file");
        }

        instance.provider.init(instance.networks, instance.ipServiceDiscovery, new PacketHandler(), new ClientPacketHandler());
    }

    public static NetworkService getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final NetworkService INSTANCE = new NetworkService();
    }

    public static record Subnet(String address, String host) { }

    public static record Network(String key, short port, String authServerHost, short authServerPort, String authServerKey, Collection<Subnet> subnets) { }
}
