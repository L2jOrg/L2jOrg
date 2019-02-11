package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.QuestState;

import java.nio.ByteBuffer;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	// format: cd
	private boolean _quest = false;
	private int _tutorialId = 0;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_quest = buffer.get() > 0;
		_tutorialId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		for(QuestState qs : player.getAllQuestsStates())
			qs.getQuest().notifyTutorialEvent("QM", _quest, String.valueOf(_tutorialId), qs);
	}
}