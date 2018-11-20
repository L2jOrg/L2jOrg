package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.QuestHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.l2.components.SystemMsg;

public class RequestQuestAbort extends L2GameClientPacket
{
	private int _questID;

	@Override
	protected void readImpl()
	{
		_questID = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		Quest quest = QuestHolder.getInstance().getQuest(_questID);
		if(activeChar == null || quest == null)
			return;

		if(!quest.canAbortByPacket())
			return;

		QuestState qs = activeChar.getQuestState(quest);
		if(qs != null && !qs.isCompleted())
		{
			if(!qs.abortQuest())
				activeChar.sendPacket(SystemMsg.THIS_QUEST_CANNOT_BE_DELETED);
		}
	}
}