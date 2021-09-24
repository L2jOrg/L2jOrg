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
package org.l2j.gameserver.network.auth;

import io.github.joealisson.mmocore.Connection;
import io.github.joealisson.mmocore.Connector;
import io.github.joealisson.mmocore.PacketHandler;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NetworkService;
import org.l2j.gameserver.network.auth.gs2as.ChangePassword;
import org.l2j.gameserver.network.auth.gs2as.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.network.auth.gs2as.ServerStatus.SERVER_LIST_TYPE;

/**
 * @author JoeAlisson
 */
public class AuthService implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final Map<String, GameClient> waitingClients = new ConcurrentHashMap<>();
    private final Map<String, GameClient> authedClients = new ConcurrentHashMap<>();
    private final NetworkService.Network network;

    private AuthServerClient client;
    private final Connector<AuthServerClient> connector;
    private volatile boolean shutdown = false;
    private int authKey;

    public AuthService(NetworkService.Network network, PacketHandler<AuthServerClient> packetHandler) {
        this.network = network;
        connector = Connector.create(this::createClient, packetHandler, ThreadPool::execute);
    }

    private AuthServerClient createClient(Connection<AuthServerClient> tConnection) {
        return new AuthServerClient(tConnection, this);
    }

    public void connect() throws IOException, ExecutionException, InterruptedException {
        InetSocketAddress address;
        if(isNullOrEmpty(network.authServerHost())) {
            LOGGER.warn("Auth server address not configured trying to connect to localhost");
            address = new InetSocketAddress(network.authServerPort());
        } else {
            address = new InetSocketAddress(network.authServerHost(), network.authServerPort());
        }
        if(nonNull(client)) {
            client.close();
        }
        LOGGER.info("Connecting to auth server on {}", address);
        client = connector.connect(address);

    }

    @Override
    public void run() {
        try {
            if(!shutdown && (isNull(client) || !client.isConnected())) {
                connect();
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOGGER.debug(e.getMessage(), e);
            restart();
        }
    }

    public GameClient addWaitingClient(GameClient client) {
        return waitingClients.put(client.getAccountName(), client);
    }

    public GameClient removeWaitingClient(String account) {
        return waitingClients.remove(account);
    }

    public GameClient addAuthedClient(GameClient client) {
        return authedClients.put(client.getAccountName(), client);
    }

    public GameClient removeAuthedClient(String login) {
        return authedClients.remove(login);
    }

    public GameClient getAuthedClient(String login) {
        return authedClients.get(login);
    }

    public String[] getAccounts() {
        return authedClients.isEmpty() ? Util.STRING_ARRAY_EMPTY : authedClients.keySet().toArray(String[]::new);
    }

    public void shutdown() {
        shutdown = true;
        if(nonNull(client) && client.isConnected()) {
            client.close();
        }
    }

    public void sendPacket(SendablePacket packet) {
        if(nonNull(client) && client.isConnected()) {
            client.sendPacket(packet);
        }
    }

    public void restart() {
        restart(5);
    }

    public void restart(int waitSeconds) {
        if(nonNull(client)) {
            client.close(null);
        }
        client = null;
        ThreadPool.schedule(this, waitSeconds, TimeUnit.SECONDS);
    }

    public void sendChangePassword(String accountName, String oldPass, String curpass) {
        sendPacket(new ChangePassword(accountName, oldPass, curpass, ""));
    }

    public void sendServerType(int type) {
        sendPacket(new ServerStatus().add(SERVER_LIST_TYPE, type));
    }

    public NetworkService.Network network() {
        return network;
    }

    public int getAuthKey() {
        return authKey;
    }

    public void setAuthKey(int authKey) {
        this.authKey = authKey;
    }
}
