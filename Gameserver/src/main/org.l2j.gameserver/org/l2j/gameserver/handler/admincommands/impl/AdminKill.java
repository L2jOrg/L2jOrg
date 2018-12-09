package org.l2j.gameserver.handler.admincommands.impl;

import org.l2j.commons.util.Converter;
import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;

public class AdminKill implements IAdminCommandHandler
{
	private enum Commands
	{
		admin_kill,
		admin_damage,
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_kill:
				if(wordList.length == 1)
					handleKill(activeChar);
				else
					handleKill(activeChar, wordList[1]);
				break;
			case admin_damage:
				handleDamage(activeChar, Converter.stringToInt(wordList[1], 1));
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleKill(Player activeChar)
	{
		handleKill(activeChar, null);
	}

	private void handleKill(Player activeChar, String player)
	{
		GameObject obj = activeChar.getTarget();
		if(player != null)
		{
			Player plyr = World.getPlayer(player);
			if(plyr != null)
				obj = plyr;
			else
			{
				int radius = Math.max(Integer.parseInt(player), 100);
				for(Creature character : activeChar.getAroundCharacters(radius, 200))
				{
					if(!character.isDoor())
					{
						if(character.isNpc())
							((NpcInstance) character).getAggroList().addDamageHate(activeChar, (int) character.getCurrentHp(), 0);
						character.doDie(activeChar);
					}
				}
				activeChar.sendMessage("Killed within " + radius + " unit radius.");
				return;
			}
		}

		if(obj != null && obj.isCreature())
		{
			Creature target = (Creature) obj;
			target.doDie(activeChar);
		}
		else
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
	}

	private void handleDamage(Player activeChar, int damage)
	{
		GameObject obj = activeChar.getTarget();

		if(obj == null)
		{
			activeChar.sendPacket(SystemMsg.SELECT_TARGET);
			return;
		}

		if(!obj.isCreature())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Creature cha = (Creature) obj;
		cha.reduceCurrentHp(damage, activeChar, null, true, true, false, false, false, false, true);
		activeChar.sendMessage("You gave " + damage + " damage to " + cha.getName() + ".");
	}
}