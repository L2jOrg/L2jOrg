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
package org.l2j.authserver.network.client.packet.auth2client;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.AuthServerPacket;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.l2j.authserver.settings.AuthServerSettings;

/**
 *
 * TYPE_BARE=0 - Indicates that each game server will have its basic information specified.
 * 
 * TYPE_C0=1 - Indicates that each game server will have its additional and dynamic information specified.
 * 
 * TYPE_NAMED=2 - Indicates that each game server will have its name specified.
 * 
 * TYPE_C1=3 - Indicates that each game server will have its type mask specified.
 * 
 * TYPE_C2=4 - Indicates that each game server will have its bracket flag specified.
 * 
 * TYPE_FREYA=5 - Indicates that each game server will have reader's character count(s) specified.
 *
 *
 * Server will be considered as
 *  Good when the number of online players is less than half the maximum.
 *  as Normal between half and 4/5 and Full when there's more than 4/5 of the maximum number of players
 */
public final class ServerList extends AuthServerPacket {

    private final byte listType;

    public ServerList(byte listType) {
        this.listType = listType;
    }

    @Override
    public void writeImpl(AuthClient client, WritableBuffer buffer) {
        var servers = GameServerManager.getInstance().getRegisteredGameServers();
        buffer.writeByte(0x04);
        buffer.writeByte(servers.size());
        buffer.writeByte(client.getLastServer());

        for (var server : servers.values()) {
            buffer.writeByte(server.getId());

            byte[] address = server.getAddressFor(client.getHostAddress());

            buffer.writeBytes(address);
            buffer.writeInt(server.getPort());
            buffer.writeByte(server.getAgeLimit()); // minimum age
            buffer.writeByte(server.isPvp());
            buffer.writeShort(server.getOnlinePlayersCount());
            buffer.writeShort(server.getMaxPlayers());

            var status = server.getStatus();
            if(ServerStatus.STATUS_GM_ONLY == status && client.getAccessLevel() < AuthServerSettings.gmMinimumLevel()) {
                status = ServerStatus.STATUS_DOWN;
            }

            buffer.writeByte(ServerStatus.STATUS_DOWN != status);
            buffer.writeInt(server.getServerType());
            buffer.writeByte(server.isShowingBrackets()); // Region
        }

        buffer.writeShort(0xa4);
        for (var server : servers.values()) {
            buffer.writeByte(server.getId());
            buffer.writeByte(client.getPlayersOnServer(server.getId()));
        }
    }
}



