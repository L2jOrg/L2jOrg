package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.L2GameClient;
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.MON_RACE_INFO);

        writeInt(_unknown1);
        writeInt(_unknown2);
        writeInt(0x08);

        for (int i = 0; i < 8; i++) {
            writeInt(_monsters[i].getObjectId()); // npcObjectID
            writeInt(_monsters[i].getTemplate().getId() + 1000000); // npcID
            writeInt(14107); // origin X
            writeInt(181875 + (58 * (7 - i))); // origin Y
            writeInt(-3566); // origin Z
            writeInt(12080); // end X
            writeInt(181875 + (58 * (7 - i))); // end Y
            writeInt(-3566); // end Z
            writeDouble(_monsters[i].getTemplate().getfCollisionHeight()); // coll. height
            writeDouble(_monsters[i].getTemplate().getfCollisionRadius()); // coll. radius
            writeInt(120); // ?? unknown
            for (int j = 0; j < 20; j++) {
                if (_unknown1 == 0) {
                    writeByte((byte) _speeds[i][j]);
                } else {
                    writeByte((byte) 0x00);
                }
            }
        }
    }

}
