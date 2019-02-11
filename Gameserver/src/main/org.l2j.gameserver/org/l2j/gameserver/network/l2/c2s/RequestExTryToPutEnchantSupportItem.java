package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExPutEnchantSupportItemResult;

import java.nio.ByteBuffer;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		buffer.getInt();
		buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
	}
}