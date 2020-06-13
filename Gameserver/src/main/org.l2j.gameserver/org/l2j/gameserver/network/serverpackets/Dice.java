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

public class Dice extends ServerPacket {
    private final int _charObjId;
    private final int _itemId;
    private final int _number;
    private final int _x;
    private final int _y;
    private final int _z;

    public Dice(int charObjId, int itemId, int number, int x, int y, int z) {
        _charObjId = charObjId;
        _itemId = itemId;
        _number = number;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.DICE);

        writeInt(_charObjId); // object id of player
        writeInt(_itemId); // item id of dice (spade) 4625,4626,4627,4628
        writeInt(_number); // number rolled
        writeInt(_x); // x
        writeInt(_y); // y
        writeInt(_z); // z
    }

}
