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
package org.l2j.gameserver.network.provider.multi;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import io.github.joealisson.mmocore.PacketHandler;
import io.github.joealisson.primitive.ArrayIntList;
import io.github.joealisson.primitive.IntList;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NetworkService;
import org.l2j.gameserver.network.NetworkServiceProvider;
import org.l2j.gameserver.network.auth.AuthServerClient;
import org.l2j.gameserver.network.auth.AuthService;
import org.l2j.gameserver.network.auth.SendablePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author JoeAlisson
 */
public class MultiNetworkProvider implements NetworkServiceProvider {

    private final List<ConnectionHandler<GameClient>> handlers = new ArrayList<>();
    private final List<AuthService> authServices = new ArrayList<>();

    @Override
    public void init(List<NetworkService.Network> networks, String ipServiceDiscoveryUrl, PacketHandler<AuthServerClient> authPacketHandler, PacketHandler<GameClient> clientPacketHandler) throws IOException {
        IntList usedPorts = new ArrayIntList(networks.size());

        for (var network : networks) {

            if(!usedPorts.contains(network.port())) {
                var handler = ConnectionBuilder.create(new InetSocketAddress(network.port()), GameClient::new,  clientPacketHandler, ThreadPool::execute).build();
                handler.start();
                handlers.add(handler);
                usedPorts.add(network.port());
            }

            var authServer = new AuthService(network, authPacketHandler);
            authServices.add(authServer);
            ThreadPool.execute(authServer);
        }
    }

    @Override
    public void shutdown() {
        closeAuthConnection();
        handlers.forEach(ConnectionHandler::shutdown);
    }

    @Override
    public void closeAuthConnection() {
        authServices.forEach(AuthService::shutdown);
    }

    @Override
    public void sendPacketToAuth(int authKey, SendablePacket packet) {
        for (var authService : authServices) {
            if(authService.getAuthKey() == authKey || authKey == -1) {
                authService.sendPacket(packet);
            }
        }
    }

    @Override
    public GameClient removeAuthedClient(int authKey, String account) {
        return mapFromAuthWithKey(authKey, account, AuthService::removeAuthedClient);
    }

    @Override
    public GameClient removeWaitingClient(int authKey, String account) {
        return mapFromAuthWithKey(authKey, account, AuthService::removeWaitingClient);
    }

    @Override
    public GameClient getAuthedClient(int authKey, String account) {
        return mapFromAuthWithKey(authKey, account, AuthService::getAuthedClient);
    }

    @Override
    public GameClient addAuthedClient(int authKey, GameClient client) {
        for (var authService : authServices) {
            if(authService.getAuthKey() == authKey) {
                return authService.addAuthedClient(client);
            }
        }
        return null;
    }

    @Override
    public GameClient addWaitingClient(int authKey, GameClient client) {
        for (var authService : authServices) {
            if(authService.getAuthKey() == authKey) {
                return authService.addWaitingClient(client);
            }
        }
        return null;
    }

    private GameClient mapFromAuthWithKey(int authKey, String account, BiFunction<AuthService, String, GameClient> mapper) {
        if(authKey <= 0) {
            return mapFromAny(account, mapper);
        }
        for (var authService : authServices) {
            if(authService.getAuthKey() == authKey ) {
                return mapper.apply(authService, account);
            }
        }
        return null;
    }

    private GameClient mapFromAny(String account, BiFunction<AuthService, String, GameClient> mapper) {
        for (var authService : authServices) {
            var client = mapper.apply(authService, account);
            if(client != null) {
                return client;
            }
        }
        return null;
    }
}
