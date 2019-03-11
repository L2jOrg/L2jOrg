package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExChangeToAwakenedClass extends IClientOutgoingPacket {
    private final int _classId;

    public ExChangeToAwakenedClass(int classId) {
        _classId = classId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHANGE_TO_AWAKENED_CLASS.writeId(packet);

        packet.putInt(_classId);
    }
}