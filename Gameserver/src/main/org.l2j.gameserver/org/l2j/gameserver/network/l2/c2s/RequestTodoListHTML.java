package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

public class RequestTodoListHTML extends L2GameClientPacket
{
	private int _tab;
	private String _linkName;

	@Override
	protected void readImpl()
	{
		_tab = readC();
		_linkName = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
	}
}