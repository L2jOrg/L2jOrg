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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class TeleportToLocation extends ServerPacket {
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;

    public TeleportToLocation(WorldObject obj, int x, int y, int z, int heading) {
        _targetObjId = obj.getObjectId();
        _x = x;
        _y = y;
        _z = z;
        _heading = heading;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.TELEPORT_TO_LOCATION, buffer );

        buffer.writeInt(_targetObjId);
        buffer.writeInt(_x);
        buffer.writeInt(_y);
        buffer.writeInt(_z);
        buffer.writeInt(0x00); // isValidation ??
        buffer.writeInt(_heading);
        buffer.writeInt(0x00); // Unknown
    }

}
