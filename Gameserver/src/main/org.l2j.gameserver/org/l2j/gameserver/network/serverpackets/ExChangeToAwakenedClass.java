package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExChangeToAwakenedClass extends ServerPacket {
    private final int _classId;

    public ExChangeToAwakenedClass(int classId) {
        _classId = classId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_TO_AWAKENED_CLASS);

        writeInt(_classId);
    }

}