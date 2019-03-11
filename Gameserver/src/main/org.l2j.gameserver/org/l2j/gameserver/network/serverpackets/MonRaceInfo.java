package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class MonRaceInfo extends IClientOutgoingPacket {
    private final int _unknown1;
    private final int _unknown2;
    private final L2Npc[] _monsters;
    private final int[][] _speeds;

    public MonRaceInfo(int unknown1, int unknown2, L2Npc[] monsters, int[][] speeds) {
        /*
         * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
         */
        _unknown1 = unknown1;
        _unknown2 = unknown2;
        _monsters = monsters;
        _speeds = speeds;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MON_RACE_INFO.writeId(packet);

        packet.putInt(_unknown1);
        packet.putInt(_unknown2);
        packet.putInt(0x08);

        for (int i = 0; i < 8; i++) {
            packet.putInt(_monsters[i].getObjectId()); // npcObjectID
            packet.putInt(_monsters[i].getTemplate().getId() + 1000000); // npcID
            packet.putInt(14107); // origin X
            packet.putInt(181875 + (58 * (7 - i))); // origin Y
            packet.putInt(-3566); // origin Z
            packet.putInt(12080); // end X
            packet.putInt(181875 + (58 * (7 - i))); // end Y
            packet.putInt(-3566); // end Z
            packet.putDouble(_monsters[i].getTemplate().getfCollisionHeight()); // coll. height
            packet.putDouble(_monsters[i].getTemplate().getfCollisionRadius()); // coll. radius
            packet.putInt(120); // ?? unknown
            for (int j = 0; j < 20; j++) {
                if (_unknown1 == 0) {
                    packet.put((byte) _speeds[i][j]);
                } else {
                    packet.put((byte) 0x00);
                }
            }
        }
    }
}
