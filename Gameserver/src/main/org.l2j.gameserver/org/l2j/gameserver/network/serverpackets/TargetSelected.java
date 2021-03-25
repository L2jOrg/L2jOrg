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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class TargetSelected extends ServerPacket {
    private final int _objectId;
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;

    /**
     * @param objectId
     * @param targetId
     * @param x
     * @param y
     * @param z
     */
    public TargetSelected(int objectId, int targetId, int x, int y, int z) {
        _objectId = objectId;
        _targetObjId = targetId;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.TARGET_SELECTED, buffer );

        buffer.writeInt(_objectId);
        buffer.writeInt(_targetObjId);
        buffer.writeInt(_x);
        buffer.writeInt(_y);
        buffer.writeInt(_z);
        buffer.writeInt(0x00); // ?
    }

}
