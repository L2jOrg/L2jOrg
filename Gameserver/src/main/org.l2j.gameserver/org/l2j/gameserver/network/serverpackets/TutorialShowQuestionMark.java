package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Mobius
 */
public final class TutorialShowQuestionMark extends ServerPacket {
    private final int _markId;
    private final int _markType;

    public TutorialShowQuestionMark(int markId, int markType) {
        _markId = markId;
        _markType = markType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TUTORIAL_SHOW_QUESTION_MARK);

        writeByte((byte) _markType);
        writeInt(_markId);
    }

}