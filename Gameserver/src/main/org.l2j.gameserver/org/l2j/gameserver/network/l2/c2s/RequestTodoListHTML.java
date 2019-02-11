package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestTodoListHTML extends L2GameClientPacket
{
	private int _tab;
	private String _linkName;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_tab = buffer.get();
		_linkName = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
	}
}