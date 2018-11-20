package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

public class RequestCrystallizeItemCancel extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//TODO
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		activeChar.sendActionFailed();
	}
}
