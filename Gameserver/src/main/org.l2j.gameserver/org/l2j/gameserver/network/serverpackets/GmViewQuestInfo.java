package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Tempy
 */
public class GmViewQuestInfo extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private final List<Quest> _questList;

    public GmViewQuestInfo(L2PcInstance cha) {
        _activeChar = cha;
        _questList = cha.getAllActiveQuests();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_QUEST_INFO.writeId(packet);
        writeString(_activeChar.getName(), packet);
        packet.putShort((short) _questList.size()); // quest count

        for (Quest quest : _questList) {
            final QuestState qs = _activeChar.getQuestState(quest.getName());

            packet.putInt(quest.getId());
            packet.putInt(qs == null ? 0 : qs.getCond());
        }
        packet.putShort((short) 0x00); // some size
        // for size; ddQQ
    }
}
