package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public class AdminLevel implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_add_level,
		admin_addLevel,
		admin_set_level,
		admin_setLevel,
	}

	private void setLevel(Player activeChar, GameObject target, int level)
	{
		if(target == null || !(target.isPlayer() || target.isPet()))
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}
		if(level < 1 || level > activeChar.getMaxLevel())
		{
			activeChar.sendMessage("You must specify level 1 - " + activeChar.getMaxLevel());
			return;
		}
		if(target.isPlayer())
		{
			Player player = (Player) target;
			Long exp_add = Experience.getExpForLevel(level) - player.getExp();
			player.addExpAndSp(exp_add, 0, true);
		}
		else if(target.isPet())
		{
			PetInstance pet = (PetInstance) target;
			Long exp_add = pet.getData().getExp(level) - pet.getExp();
			pet.addExpAndSp(exp_add, 0);
		}
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		GameObject target = activeChar.getTarget();
		if(target == null || !(target.isPlayer() || target.isPet()))
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
		int level;

		switch(command)
		{
			case admin_add_level:
			case admin_addLevel:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //addLevel level");
					return false;
				}
				try
				{
					level = Integer.parseInt(wordList[1]);
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("You must specify level");
					return false;
				}
				setLevel(activeChar, target, level + ((Creature) target).getLevel());
				break;
			case admin_set_level:
			case admin_setLevel:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //setLevel level");
					return false;
				}
				try
				{
					level = Integer.parseInt(wordList[1]);
				}
				catch(NumberFormatException e)
				{
					activeChar.sendMessage("You must specify level");
					return false;
				}
				setLevel(activeChar, target, level);
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}