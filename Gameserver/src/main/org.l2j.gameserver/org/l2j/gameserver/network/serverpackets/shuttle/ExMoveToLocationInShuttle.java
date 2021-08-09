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
package org.l2j.gameserver.network.serverpackets.shuttle;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExMoveToLocationInShuttle extends ServerPacket {
    private final int _charObjId;
    private final int _airShipId;
    private final int _targetX;
    private final int _targetY;
    private final int _targetZ;
    private final int _fromX;
    private final int _fromY;
    private final int _fromZ;

    public ExMoveToLocationInShuttle(Player player, int fromX, int fromY, int fromZ) {
        _charObjId = player.getObjectId();
        _airShipId = player.getShuttle().getObjectId();
        _targetX = player.getInVehiclePosition().getX();
        _targetY = player.getInVehiclePosition().getY();
        _targetZ = player.getInVehiclePosition().getZ();
        _fromX = fromX;
        _fromY = fromY;
        _fromZ = fromZ;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_MOVE_TO_LOCATION_IN_SHUTTLE, buffer );

        buffer.writeInt(_charObjId);
        buffer.writeInt(_airShipId);
        buffer.writeInt(_targetX);
        buffer.writeInt(_targetY);
        buffer.writeInt(_targetZ);
        buffer.writeInt(_fromX);
        buffer.writeInt(_fromY);
        buffer.writeInt(_fromZ);
    }

}
