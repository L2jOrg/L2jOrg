package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestAddExpandQuestAlarm extends IClientIncomingPacket {
    private int _questId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _questId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Quest quest = QuestManager.getInstance().getQuest(_questId);
        if (quest != null) {
            quest.sendNpcLogList(activeChar);
        }
    }
}
