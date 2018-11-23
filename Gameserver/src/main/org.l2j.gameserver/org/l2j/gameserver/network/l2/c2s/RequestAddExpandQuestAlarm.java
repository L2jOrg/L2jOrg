package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.QuestHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.l2.s2c.ExQuestNpcLogList;

/**
 * @author VISTALL
 * @date 14:47/26.02.2011
 */
public class RequestAddExpandQuestAlarm extends L2GameClientPacket
{
	private int _questId;

	@Override
	protected void readImpl() throws Exception
	{
		_questId = readInt();
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Quest quest = QuestHolder.getInstance().getQuest(_questId);
		if(quest == null)
			return;

		QuestState state = player.getQuestState(quest);
		if(state == null)
			return;

		player.sendPacket(new ExQuestNpcLogList(state));
	}
}