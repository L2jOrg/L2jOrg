package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class Appearing extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		final Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isLogoutStarted())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getObserverMode() == Player.OBSERVER_STARTING)
		{
			activeChar.appearObserverMode();
			return;
		}

		if(activeChar.getObserverMode() == Player.OBSERVER_LEAVING)
		{
			activeChar.returnFromObserverMode();
			return;
		}

		if(!activeChar.isTeleporting())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.onTeleported();
	}
}