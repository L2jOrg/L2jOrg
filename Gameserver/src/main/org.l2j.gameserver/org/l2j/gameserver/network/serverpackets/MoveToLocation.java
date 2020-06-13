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

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class MoveToLocation extends ServerPacket {
    private final int _charObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _xDst;
    private final int _yDst;
    private final int _zDst;

    public MoveToLocation(Creature cha) {
        _charObjId = cha.getObjectId();
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
        _xDst = cha.getXdestination();
        _yDst = cha.getYdestination();
        _zDst = cha.getZdestination();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.MOVE_TO_LOCATION);

        writeInt(_charObjId);

        writeInt(_xDst);
        writeInt(_yDst);
        writeInt(_zDst);

        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
    }

}
