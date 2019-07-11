package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Tempy
 */
public class GmViewQuestInfo extends ServerPacket {
    private final Player _activeChar;
    private final List<Quest> _questList;

    public GmViewQuestInfo(Player cha) {
        _activeChar = cha;
        _questList = cha.getAllActiveQuests();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.GM_VIEW_QUEST_INFO);
        writeString(_activeChar.getName());
        writeShort((short) _questList.size()); // quest count

        for (Quest quest : _questList) {
            final QuestState qs = _activeChar.getQuestState(quest.getName());

            writeInt(quest.getId());
            writeInt(qs == null ? 0 : qs.getCond());
        }
        writeShort((short) 0x00); // some size
        // for size; ddQQ
    }

}
