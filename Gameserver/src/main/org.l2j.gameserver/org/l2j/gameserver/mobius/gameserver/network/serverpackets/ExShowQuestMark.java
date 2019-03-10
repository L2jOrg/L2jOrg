package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_QUEST_MARK.writeId(packet);

        packet.putInt(_questId);
        packet.putInt(_questState);
    }
}
