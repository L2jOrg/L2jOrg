package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;

/**
 * @author Sdw
 */
public class RequestAddExpandQuestAlarm extends ClientPacket {
    private int _questId;

    @Override
    public void readImpl() {
        _questId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Quest quest = QuestManager.getInstance().getQuest(_questId);
        if (quest != null) {
            quest.sendNpcLogList(activeChar);
        }
    }
}
