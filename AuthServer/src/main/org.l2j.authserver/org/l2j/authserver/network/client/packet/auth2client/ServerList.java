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
package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.controller.GameServerManager;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.AuthServerPacket;
import org.l2j.authserver.network.gameserver.packet.game2auth.ServerStatus;
import org.l2j.authserver.settings.AuthServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 *
 *  * TYPE_BARE=0 - Indicates that each game server will have its basic information specified.
 *  *
 *  * TYPE_C0=1 - Indicates that each game server will have its additional and dynamic information specified.
 *  *
 *  * TYPE_NAMED=2 - Indicates that each game server will have its name specified.
 *  *
 *  * TYPE_C1=3 - Indicates that each game server will have its type mask specified.
 *  *
 *  * TYPE_C2=4 - Indicates that each game server will have its bracket flag specified.
 *  *
 *  * TYPE_FREYA=5 - Indicates that each game server will have reader's character count(s) specified.
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
    public void writeImpl(AuthClient client) {
        var servers = GameServerManager.getInstance().getRegisteredGameServers();
        writeByte((byte)0x04);
        writeByte((byte)servers.size());
        writeByte((byte)client.getLastServer());

        for (var server : servers.values()) {
            writeByte((byte)server.getId());

            byte[] address = server.getAddressFor(client.getHostAddress());

            writeBytes(address);
            writeInt(server.getPort());
            writeByte(server.getAgeLimit()); // minimum age
            writeByte(server.isPvp());
            writeShort((short) server.getOnlinePlayersCount());
            writeShort((short) server.getMaxPlayers());

            var status = server.getStatus();
            if(ServerStatus.STATUS_GM_ONLY == status && client.getAccessLevel() < getSettings(AuthServerSettings.class).gmMinimumLevel()) {
                status = ServerStatus.STATUS_DOWN;
            }

            writeByte((byte) (ServerStatus.STATUS_DOWN == status ? 0x00 : 0x01));
            writeInt(server.getServerType());
            writeByte((byte) (server.isShowingBrackets() ? 0x01 : 0x00)); // Region
        }

        writeShort((short)0xa4);
        for (var server : servers.values()) {
            writeByte((byte)server.getId());
            writeByte((byte)client.getPlayersOnServer(server.getId()));
        }
    }
}
