package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;

@SuppressWarnings("unused")
public class AdminTarget implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_target
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanViewChar)
			return false;

		try
		{
			String targetName = wordList[1];
			GameObject obj = World.getPlayer(targetName);
			if(obj != null && obj.isPlayer())
				obj.onAction(activeChar, false);
			else
				activeChar.sendMessage("Player " + targetName + " not found");
		}
		catch(IndexOutOfBoundsException e)
		{
			activeChar.sendMessage("Please specify correct name.");
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}