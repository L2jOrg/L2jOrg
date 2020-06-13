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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author mrTJO
 */
public class ExCubeGameTeamList extends ServerPacket {
    // Players Lists
    private final List<Player> _bluePlayers;
    private final List<Player> _redPlayers;

    // Common Values
    private final int _roomNumber;

    /**
     * Show Minigame Waiting List to Player
     *
     * @param redPlayers  Red Players List
     * @param bluePlayers Blue Players List
     * @param roomNumber  Arena/Room ID
     */
    public ExCubeGameTeamList(List<Player> redPlayers, List<Player> bluePlayers, int roomNumber) {
        _redPlayers = redPlayers;
        _bluePlayers = bluePlayers;
        _roomNumber = roomNumber - 1;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);

        writeInt(0x00);

        writeInt(_roomNumber);
        writeInt(0xffffffff);

        writeInt(_bluePlayers.size());
        for (Player player : _bluePlayers) {
            writeInt(player.getObjectId());
            writeString(player.getName());
        }
        writeInt(_redPlayers.size());
        for (Player player : _redPlayers) {
            writeInt(player.getObjectId());
            writeString(player.getName());
        }
    }

}