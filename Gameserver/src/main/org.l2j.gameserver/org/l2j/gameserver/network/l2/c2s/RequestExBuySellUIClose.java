package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestExBuySellUIClose extends L2GameClientPacket
{
	@Override
	protected void runImpl()
	{
		// trigger
	}

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setBuyListId(0);
		activeChar.sendItemList(true);
	}
}