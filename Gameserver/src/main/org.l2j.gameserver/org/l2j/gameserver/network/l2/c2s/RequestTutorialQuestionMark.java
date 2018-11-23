package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.QuestState;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	// format: cd
	private boolean _quest = false;
	private int _tutorialId = 0;

	@Override
	protected void readImpl()
	{
		_quest = readByte() > 0;
		_tutorialId = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		for(QuestState qs : player.getAllQuestsStates())
			qs.getQuest().notifyTutorialEvent("QM", _quest, String.valueOf(_tutorialId), qs);
	}
}