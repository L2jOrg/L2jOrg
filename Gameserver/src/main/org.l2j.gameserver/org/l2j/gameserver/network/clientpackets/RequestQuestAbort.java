package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestAbort;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.QuestList;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestQuestAbort extends ClientPacket {
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

        final Quest qe = QuestManager.getInstance().getQuest(_questId);
        if (qe != null) {
            final QuestState qs = activeChar.getQuestState(qe.getName());
            if (qs != null) {
                qs.setSimulated(false);
                qs.exitQuest(QuestType.REPEATABLE);
                activeChar.sendPacket(new QuestList(activeChar));
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestAbort(activeChar, _questId), activeChar, Listeners.players());
                qe.onQuestAborted(activeChar);
            }
        }
    }
}