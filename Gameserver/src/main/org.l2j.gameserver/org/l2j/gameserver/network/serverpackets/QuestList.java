package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class QuestList extends IClientOutgoingPacket {
    private final List<QuestState> _activeQuests;
    private final byte[] _oneTimeQuestMask;

    public QuestList(L2PcInstance player) {
        _activeQuests = new LinkedList<>();
        _oneTimeQuestMask = new byte[128];

        for (QuestState qs : player.getAllQuestStates()) {
            final int questId = qs.getQuest().getId();
            if (questId > 0) {
                if (qs.isStarted()) {
                    _activeQuests.add(qs);
                } else if (qs.isCompleted() && !(((questId > 255) && (questId < 10256)) || (questId > 11023))) {
                    _oneTimeQuestMask[(questId % 10000) / 8] |= 1 << (questId % 8);
                }
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.QUEST_LIST.writeId(packet);
        packet.putShort((short) _activeQuests.size());
        for (QuestState qs : _activeQuests) {
            packet.putInt(qs.getQuest().getId());
            packet.putInt(qs.getCond());
        }
        packet.put(_oneTimeQuestMask);
    }

    @Override
    protected int size(L2GameClient client) {
        return 6 + _activeQuests.size() * 8;
    }
}
