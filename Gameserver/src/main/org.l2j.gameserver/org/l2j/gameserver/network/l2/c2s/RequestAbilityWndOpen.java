package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExShowAPListWnd;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class RequestAbilityWndOpen extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(ExShowAPListWnd.STATIC);
	}
}