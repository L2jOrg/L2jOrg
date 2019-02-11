package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExDuelAskStart extends L2GameServerPacket
{
	String _requestor;
	int _isPartyDuel;

	public ExDuelAskStart(String requestor, int isPartyDuel)
	{
		_requestor = requestor;
		_isPartyDuel = isPartyDuel;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_requestor, buffer);
		buffer.putInt(_isPartyDuel);
	}
}