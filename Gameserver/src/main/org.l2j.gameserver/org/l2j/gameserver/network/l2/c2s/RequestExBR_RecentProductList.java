package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExBR_RecentProductListPacket;

public class RequestExBR_RecentProductList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
		// триггер
	}

	@Override
	public void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExBR_RecentProductListPacket(activeChar));
	}
}