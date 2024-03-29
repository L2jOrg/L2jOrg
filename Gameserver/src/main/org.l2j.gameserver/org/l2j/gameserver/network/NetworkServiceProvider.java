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

import io.github.joealisson.mmocore.PacketHandler;
import org.l2j.gameserver.network.auth.AuthServerClient;
import org.l2j.gameserver.network.auth.SendablePacket;

import java.io.IOException;
import java.util.List;

/**
 * @author JoeAlisson
 */
public interface NetworkServiceProvider {

    void init(List<NetworkService.Network> networks, String ipServiceDiscoveryUrl, PacketHandler<AuthServerClient> authPacketHandler, PacketHandler<GameClient> clientPacketHandler) throws IOException;

    void shutdown();

    void closeAuthConnection();

    void sendPacketToAuth(int authKey, SendablePacket packet);

    GameClient removeAuthedClient(int authKey, String account);

    GameClient removeWaitingClient(int authKey, String account);

    GameClient getAuthedClient(int authKey, String account);

    GameClient addAuthedClient(int authKey, GameClient client);

    GameClient addWaitingClient(int authKey, GameClient client);
}
