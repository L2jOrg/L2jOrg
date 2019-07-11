package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Luca Baldi
 */
public class ExShowQuestMark extends ServerPacket {
    private final int _questId;
    private final int _questState;

    public ExShowQuestMark(int questId, int questState) {
        _questId = questId;
        _questState = questState;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_QUEST_MARK);

        writeInt(_questId);
        writeInt(_questState);
    }

}
