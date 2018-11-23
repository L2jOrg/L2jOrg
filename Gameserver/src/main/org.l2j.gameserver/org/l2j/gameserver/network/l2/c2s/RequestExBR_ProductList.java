package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExBR_ProductListPacket;

public class RequestExBR_ProductList extends L2GameClientPacket
{
	private int _unk;

	@Override
	protected void readImpl()
	{
		_unk = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		// Purchase History comes last
		if(_unk == 1)
		{
			activeChar.sendPacket(new ExBR_ProductListPacket(activeChar, false)); // Обычный список.
			activeChar.sendPacket(new ExBR_ProductListPacket(activeChar, true)); // История покупок.
		}
	}
}