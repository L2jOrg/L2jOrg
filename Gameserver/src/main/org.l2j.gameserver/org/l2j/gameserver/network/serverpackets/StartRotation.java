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

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class StartRotation extends ServerPacket {
    private final int _charObjId;
    private final int _degree;
    private final int _side;
    private final int _speed;

    public StartRotation(int objectId, int degree, int side, int speed) {
        _charObjId = objectId;
        _degree = degree;
        _side = side;
        _speed = speed;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.START_ROTATING);

        writeInt(_charObjId);
        writeInt(_degree);
        writeInt(_side);
        writeInt(_speed);
    }

}
