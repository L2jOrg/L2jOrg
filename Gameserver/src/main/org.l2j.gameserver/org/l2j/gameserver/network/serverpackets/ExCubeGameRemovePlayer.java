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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO
 */
public class ExCubeGameRemovePlayer extends ServerPacket {
    Player _player;
    boolean _isRedTeam;

    /**
     * Remove Player from Minigame Waiting List
     *
     * @param player    Player to Remove
     * @param isRedTeam Is Player from Red Team?
     */
    public ExCubeGameRemovePlayer(Player player, boolean isRedTeam) {
        _player = player;
        _isRedTeam = isRedTeam;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST, buffer );

        buffer.writeInt(0x02);

        buffer.writeInt(0xffffffff);

        buffer.writeInt(_isRedTeam ? 0x01 : 0x00);
        buffer.writeInt(_player.getObjectId());
    }

}
