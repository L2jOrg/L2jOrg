package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.QuestState;

import java.nio.ByteBuffer;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	// format: cS

	private String _bypass = null;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_bypass = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		for(QuestState qs : player.getAllQuestsStates())
			qs.getQuest().notifyTutorialEvent("BYPASS", false, _bypass, qs);
	}
}