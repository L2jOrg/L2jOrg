package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExRemoveEnchantSupportItemResult;

/**
 * @author Bonux
**/
public class RequestExRemoveEnchantSupportItem extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(ExRemoveEnchantSupportItemResult.STATIC);
	}
}