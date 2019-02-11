package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestPartyLootModification extends L2GameClientPacket
{
	private byte _mode;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_mode = (byte) buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(_mode < 0 || _mode > Party.ITEM_ORDER_SPOIL)
			return;

		Party party = activeChar.getParty();
		if(party == null || _mode == party.getLootDistribution() || party.getPartyLeader() != activeChar)
			return;

		party.requestLootChange(_mode);
	}
}
