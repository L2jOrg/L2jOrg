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

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import io.github.joealisson.primitive.ArrayIntList;
import io.github.joealisson.primitive.IntList;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.FileUtil;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.network.auth.AuthNetworkService;
import org.l2j.gameserver.network.auth.PacketHandler;
import org.l2j.gameserver.network.auth.SendablePacket;
import org.l2j.gameserver.network.auth.gs2as.ServerStatus;
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

import static org.l2j.gameserver.network.auth.gs2as.ServerStatus.SERVER_LIST_TYPE;

/**
 * @author JoeAlisson
 */
public class NetworkService extends XmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkService.class);

    private String ipServiceDiscovery;
    private final Set<Network> networks = new HashSet<>();
    private final Collection<ConnectionHandler<GameClient>> connectionHandlers = new ArrayList<>();
    private final Collection<AuthNetworkService> authServers = new ArrayList<>();

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

        for(var node = networksNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            parseNetwork(node);
        }
    }

    private void parseNetwork(Node node) {
        var subnets = parseNetworkAddress(node.getFirstChild());

        var attr = node.getAttributes();
        var ports = parseShortArray(attr, "ports");
        var authServerHost = parseString(attr, "auth-server-host");
        var authServerPort = parseShort(attr, "auth-server-port");
        var authServerKey = parseString(attr, "auth-server-key");

        var network = new Network(ports, authServerHost, authServerPort, authServerKey, subnets);

        if(networks.add(network)) {
            LOGGER.info("add new network on ports {} to auth server {}:{}",  Arrays.toString(ports), authServerHost, authServerPort);
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

            if("$external".equals(host)) {
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
        connectionHandlers.forEach(ConnectionHandler::shutdown);
    }

    public void closeAuthServerConnection() {
        authServers.forEach(AuthNetworkService::shutdown);
    }

    public void sendPacketToAuthServer(SendablePacket packet) {
        for (var authServer : authServers) {
            authServer.sendPacket(packet);
        }
    }

    public GameClient removeAuthedClient(String account) {
        for (var authServer : authServers) {
            var client = authServer.removeAuthedClient(account);
            if(client != null) {
                return client;
            }
        }
        return null;
    }

    public GameClient removeWaitingClient(String account) {
        for (var authServer : authServers) {
            var client = authServer.removeWaitingClient(account);
            if(client != null) {
                return client;
            }
        }
        return null;
    }

    public GameClient getAuthedClient(String account) {
        for (var authServer : authServers) {
            var client = authServer.getAuthedClient(account);
            if(client != null) {
                return client;
            }
        }
        return null;
    }

    public void sendServerType(int type) {
        sendPacketToAuthServer(new ServerStatus().add(SERVER_LIST_TYPE, type));
    }

    public static void init() throws IOException {
        var instance = getInstance();
        instance.load();

        IntList usedPorts = new ArrayIntList(instance.networks.size());
        var packetHandler = new ClientPacketHandler();
        var authPacketHandler = new PacketHandler();
        for (Network network : instance.networks) {

            for (short port : network.ports) {
                if(!usedPorts.contains(port)) {
                    var handler = ConnectionBuilder.create(new InetSocketAddress(port), GameClient::new,  packetHandler, ThreadPool::execute).build();
                    handler.start();
                    instance.connectionHandlers.add(handler);
                    usedPorts.add(port);
                }
            }

            var authServer = new AuthNetworkService(network, authPacketHandler);
            instance.authServers.add(authServer);
            ThreadPool.execute(authServer);
        }
    }

    public static NetworkService getInstance() {
        return Singleton.INSTANCE;
    }

    public void sendChangePassword(String accountName, String curpass, String newpass) {
        for (AuthNetworkService authServer : authServers) {
            authServer.sendChangePassword(accountName, curpass, newpass);
        }
    }

    private static class Singleton {
        private static final NetworkService INSTANCE = new NetworkService();
    }

    public static record Subnet(String address, String host) { }

    public static record Network(short[] ports, String authServerHost, short authServerPort, String authServerKey, Collection<Subnet> subnets) { }
}
