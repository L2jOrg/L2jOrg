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
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class MonRaceInfo extends ServerPacket {
    private final int _unknown1;
    private final int _unknown2;
    private final Npc[] _monsters;
    private final int[][] _speeds;

    public MonRaceInfo(int unknown1, int unknown2, Npc[] monsters, int[][] speeds) {
        /*
         * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
         */
        _unknown1 = unknown1;
        _unknown2 = unknown2;
        _monsters = monsters;
        _speeds = speeds;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.MONRACE_INFO, buffer );

        buffer.writeInt(_unknown1);
        buffer.writeInt(_unknown2);
        buffer.writeInt(0x08);

        for (int i = 0; i < 8; i++) {
            buffer.writeInt(_monsters[i].getObjectId()); // npcObjectID
            buffer.writeInt(_monsters[i].getTemplate().getId() + 1000000); // npcID
            buffer.writeInt(14107); // origin X
            buffer.writeInt(181875 + (58 * (7 - i))); // origin Y
            buffer.writeInt(-3566); // origin Z
            buffer.writeInt(12080); // end X
            buffer.writeInt(181875 + (58 * (7 - i))); // end Y
            buffer.writeInt(-3566); // end Z
            buffer.writeDouble(_monsters[i].getTemplate().getfCollisionHeight()); // coll. height
            buffer.writeDouble(_monsters[i].getTemplate().getfCollisionRadius()); // coll. radius
            buffer.writeInt(120); // ?? unknown
            for (int j = 0; j < 20; j++) {
                if (_unknown1 == 0) {
                    buffer.writeByte(_speeds[i][j]);
                } else {
                    buffer.writeByte(0x00);
                }
            }
        }
    }

}
