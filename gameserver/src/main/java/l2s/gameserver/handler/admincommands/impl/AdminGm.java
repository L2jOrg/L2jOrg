package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;

/**
 * This class handles following admin commands: - gm = turns gm mode on/off
 */
public class AdminGm implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_gm
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		// TODO зачем отключено?
		if(Boolean.TRUE)
			return false;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		switch(command)
		{
			case admin_gm:
				handleGm(activeChar);
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleGm(Player activeChar)
	{
		if(activeChar.isGM())
		{
			activeChar.getPlayerAccess().IsGM = false;
			activeChar.sendMessage("You no longer have GM status.");
		}
		else
		{
			activeChar.getPlayerAccess().IsGM = true;
			activeChar.sendMessage("You have GM status now.");
		}
	}
}