package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestAbort;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.QuestList;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestQuestAbort extends IClientIncomingPacket {
    private int _questId;

    @Override
    public void readImpl() {
        _questId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Quest qe = QuestManager.getInstance().getQuest(_questId);
        if (qe != null) {
            final QuestState qs = activeChar.getQuestState(qe.getName());
            if (qs != null) {
                qs.setSimulated(false);
                qs.exitQuest(QuestType.REPEATABLE);
                activeChar.sendPacket(new QuestList(activeChar));
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestAbort(activeChar, _questId), activeChar, Containers.Players());
                qe.onQuestAborted(activeChar);
            }
        }
    }
}