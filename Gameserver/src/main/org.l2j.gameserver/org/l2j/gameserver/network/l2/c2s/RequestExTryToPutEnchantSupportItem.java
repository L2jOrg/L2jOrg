package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExPutEnchantSupportItemResult;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		readInt();
		readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
	}
}