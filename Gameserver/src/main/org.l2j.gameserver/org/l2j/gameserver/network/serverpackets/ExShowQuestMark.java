package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Luca Baldi
 */
public class ExShowQuestMark extends IClientOutgoingPacket {
    private final int _questId;
    private final int _questState;

    public ExShowQuestMark(int questId, int questState) {
        _questId = questId;
        _questState = questState;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHOW_QUEST_MARK);

        writeInt(_questId);
        writeInt(_questState);
    }

}
