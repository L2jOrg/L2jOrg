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
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class DoorStatusUpdate extends ServerPacket {
    private final Door _door;

    public DoorStatusUpdate(Door door) {
        _door = door;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.DOOR_STATUS_UPDATE, buffer );

        buffer.writeInt(_door.getObjectId());
        buffer.writeInt(!_door.isOpen());
        buffer.writeInt(_door.getDamage());
        buffer.writeInt(_door.isEnemy());
        buffer.writeInt(_door.getId());
        buffer.writeInt((int) _door.getCurrentHp());
        buffer.writeInt(_door.getMaxHp());
    }

}