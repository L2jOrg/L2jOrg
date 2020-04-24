package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.LinkedList;
import java.util.List;

public class QuestList extends ServerPacket {
    private final List<QuestState> _activeQuests;
    private final byte[] _oneTimeQuestMask;

    public QuestList(Player player) {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.QUESTLIST);
        writeShort(_activeQuests.size());
        for (QuestState qs : _activeQuests) {
            writeInt(qs.getQuest().getId());
            writeInt(qs.getCond());
        }
        writeBytes(_oneTimeQuestMask);
    }

}
