package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.SystemMsg;

public class AdminCancel implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_cancel
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		switch(command)
		{
			case admin_cancel:
				handleCancel(activeChar, wordList.length > 1 ? wordList[1] : null);
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleCancel(Player activeChar, String targetName)
	{
		GameObject obj = activeChar.getTarget();
		if(targetName != null)
		{
			Player plyr = World.getPlayer(targetName);
			if(plyr != null)
				obj = plyr;
			else
				try
				{
					int radius = Math.max(Integer.parseInt(targetName), 100);
					for(Creature character : activeChar.getAroundCharacters(radius, 200))
					{
						character.getAbnormalList().stopAll();
						if(character.isPlayer())
							character.getPlayer().deleteCubics();
					}
					activeChar.sendMessage("Apply Cancel within " + radius + " unit radius.");
					return;
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("Enter valid player name or radius");
					return;
				}
		}

		if(obj == null)
			obj = activeChar;
		if(obj.isCreature())
		{
			Creature creature = (Creature) obj;
			creature.getAbnormalList().stopAll();
			if(creature.isPlayer())
				creature.getPlayer().deleteCubics();
		}
		else
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
	}
}