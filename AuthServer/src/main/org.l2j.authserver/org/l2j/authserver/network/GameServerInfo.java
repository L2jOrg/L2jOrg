/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.authserver.network;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.data.database.ServerInfo;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.authserver.network.gameserver.packet.auth2game.KickPlayer;
import org.l2j.authserver.network.gameserver.packet.auth2game.RequestAccountInfo;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

public class GameServerInfo {

    private static final Logger logger = LoggerFactory.getLogger(GameServerInfo.class);
    private static final byte[] LOCALHOST = {127, 0, 0, 1};

    private final Set<String> accounts = new HashSet<>();

    private int _id;
    private volatile boolean _isAuthed;
    private int _status;

    // network
    private IPAddress[] hosts;
    private int _port;

    // config
    private boolean isPvp;
    private boolean _isTestServer;
    private boolean _isShowingClock;
    private boolean _isShowingBrackets;
    private int _maxPlayers;
    private int serverType;
    private ServerClient client;
    private byte ageLimit;


    public GameServerInfo(ServerInfo serverInfo) {
        this(serverInfo.getId(), null);
    }

    public GameServerInfo(int id,  ServerClient client) {
        _id = id;
       this.client = client;
        _status = ServerStatus.STATUS_DOWN;
    }

    public void setClient(ServerClient client) {
        this.client = client;
    }

    public void setHosts(String[] hosts) {
        logger.info("Updated Gameserver [{}] {} IP's:", _id, GameServerManager.getInstance().getServerNameById(_id));
        List<IPAddress> addresses = new ArrayList<>();
        for (var i = 0; i < hosts.length; i+=2) {
            try {
                addresses.add(new IPAddress(hosts[i], hosts[i+1]));
                logger.info("Address {} Subnet {}", hosts[i], hosts[i+1]);
            } catch (UnknownHostException e) {
                logger.warn("Couldn't resolve hostname", e);
            }
        }
        this.hosts = addresses.toArray(IPAddress[]::new);
    }

    public String getServerHost() {
        return client.getHostAddress();
    }

    public int getOnlinePlayersCount() {
        return accounts.size();
    }

    public void setDown() {
        setAuthed(false);
        setPort(0);
        setStatus(ServerStatus.STATUS_DOWN);
        accounts.clear();
    }

    public void sendKickPlayer(String account) {
        removeAccount(account);
        client.sendPacket(new KickPlayer(account));
    }

    public void requestAccountInfo(String account) {
        client.sendPacket(new RequestAccountInfo(account));
    }

    public void removeAccount(String account) {
        accounts.remove(account);
    }

    public boolean accountIsConnected(String account) {
        return accounts.contains(account);
    }

    public void setId(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void setAuthed(boolean isAuthed) {
        _isAuthed = isAuthed;
    }

    public boolean isAuthed() {
        return _isAuthed;
    }

    public void setStatus(int status) {
        _status = status;
    }

    public int getStatus() {
        return _status;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int port) {
        _port = port;
    }

    public void setMaxPlayers(int maxPlayers) {
        _maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return _maxPlayers;
    }

    public boolean isPvp() {
        return isPvp;
    }

    public void setTestServer(boolean val) {
        _isTestServer = val;
    }

    public boolean isTestServer() {
        return _isTestServer;
    }

    public void setShowingClock(boolean clock) {
        _isShowingClock = clock;
    }

    public boolean isShowingClock() {
        return _isShowingClock;
    }

    public void setShowingBrackets(boolean val) {
        _isShowingBrackets = val;
    }

    public boolean isShowingBrackets() {
        return _isShowingBrackets;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public int getServerType() {
        return serverType;
    }

    public void addAccounts(List<String> accounts) {
        this.accounts.addAll(accounts);
    }

    public void setAgeLimit(byte ageLimit) {
        this.ageLimit = ageLimit;
    }

    public void setIsPvp(boolean isPvp) {
        this.isPvp = isPvp;
    }

    public byte getAgeLimit() {
        return ageLimit;
    }

    public byte[] getAddressFor(String hostAddress)  {
        if(nonNull(hosts)) {
            for (IPAddress host : hosts) {
                if (host.isInSameSubnet(hostAddress)) {
                    return host.address;
                }
            }
        }
        return LOCALHOST;
    }

    static class IPAddress {

        private final byte[] mask;
        private final boolean _isIPv4;
        private final byte[] address;
        private final byte[] subnet;

        IPAddress(String address, String subnet) throws UnknownHostException {
            this.address = InetAddress.getByName(address).getAddress();

            final var index = subnet.indexOf("/");
            int bitLength = 0;

            if (index > 0) {
                bitLength = Integer.parseInt(subnet.substring(index + 1));
                subnet = subnet.substring(0, index);
            }

            this.subnet = InetAddress.getByName(subnet).getAddress();
            this.mask = getMask(index > 0 ? bitLength : this.subnet.length << 3, this.subnet.length);
            _isIPv4 = this.subnet.length == 4;

            if(bitLength > 0 && !applyMask(this.subnet)) {
                throw new UnknownHostException(subnet);
            }
        }

        private boolean applyMask(byte[] addr) {
            // V4 vs V4 or V6 vs V6 checks
            if (_isIPv4 == (addr.length == 4)) {
                for (int i = 0; i < this.subnet.length; i++) {
                    if ((addr[i] & this.mask[i]) != this.subnet[i]) {
                        return false;
                    }
                }
            }
            else {
                // check for embedded v4 in v6 addr (not done !)
                if (_isIPv4) {
                    // my V4 vs V6
                    for (int i = 0; i < this.subnet.length; i++) {
                        if ((addr[i + 12] & this.mask[i]) != this.subnet[i]) {
                            return false;
                        }
                    }
                }
                else {
                    // my V6 vs V4
                    for (int i = 0; i < this.subnet.length; i++) {
                        if ((addr[i] & this.mask[i + 12]) != this.subnet[i + 12]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private static byte[] getMask(int n, int maxLength) throws UnknownHostException {

            if ((n > (maxLength << 3)) || (n < 0))
            {
                throw new UnknownHostException("Invalid netmask: " + n);
            }

            final byte[] result = new byte[maxLength];
            for (int i = 0; i < maxLength; i++)
            {
                result[i] = (byte) 0xFF;
            }

            for (int i = (maxLength << 3) - 1; i >= n; i--)
            {
                result[i >> 3] = (byte) (result[i >> 3] << 1);
            }

            return result;
        }

        boolean isInSameSubnet(String hostAddress) {
            try {
                return applyMask(InetAddress.getByName(hostAddress).getAddress());
            } catch (UnknownHostException e) {
                return false;
            }
        }
    }
}