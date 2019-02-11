package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class AnswerPartyLootModification extends L2GameClientPacket
{
	public int _answer;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_answer = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Party party = activeChar.getParty();
		if(party != null)
			party.answerLootChangeRequest(activeChar, _answer == 1);
	}
}
