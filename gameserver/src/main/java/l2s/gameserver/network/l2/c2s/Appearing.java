package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class Appearing extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
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