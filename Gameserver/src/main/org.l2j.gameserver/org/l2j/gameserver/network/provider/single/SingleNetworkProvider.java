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
package org.l2j.gameserver.network.provider.single;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import io.github.joealisson.mmocore.PacketHandler;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NetworkService;
import org.l2j.gameserver.network.NetworkServiceProvider;
import org.l2j.gameserver.network.auth.AuthServerClient;
import org.l2j.gameserver.network.auth.AuthService;
import org.l2j.gameserver.network.auth.SendablePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author JoeAlisson
 */
public class SingleNetworkProvider implements NetworkServiceProvider {

    private ConnectionHandler<GameClient> handler;
    private AuthService authService;

    @Override
    public void init(List<NetworkService.Network> networks, String ipServiceDiscoveryUrl, PacketHandler<AuthServerClient> authPacketHandler, PacketHandler<GameClient> clientPacketHandler) throws IOException {
        var network = networks.get(0);
        handler = ConnectionBuilder.create(new InetSocketAddress(network.port()), GameClient::new,  clientPacketHandler, ThreadPool::execute).build();
        handler.start();

        authService = new AuthService(network, authPacketHandler);
        ThreadPool.execute(authService);
    }

    @Override
    public void shutdown() {
        authService.shutdown();
        handler.shutdown();
    }

    @Override
    public void closeAuthConnection() {
        authService.shutdown();
    }

    @Override
    public void sendPacketToAuth(int authKey, SendablePacket packet) {
        authService.sendPacket(packet);
    }

    @Override
    public GameClient removeAuthedClient(int authKey, String account) {
        return authService.removeAuthedClient(account);
    }

    @Override
    public GameClient removeWaitingClient(int authKey, String account) {
        return authService.removeWaitingClient(account);
    }

    @Override
    public GameClient getAuthedClient(int authKey, String account) {
        return authService.getAuthedClient(account);
    }

    @Override
    public GameClient addAuthedClient(int authKey, GameClient client) {
        return authService.addAuthedClient(client);
    }

    @Override
    public GameClient addWaitingClient(int authKey, GameClient client) {
        return authService.addWaitingClient(client);
    }
}
