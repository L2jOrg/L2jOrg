/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.BuilderUtil;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminInvul implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_invul",
		"admin_setinvul",
		"admin_undying",
		"admin_setundying"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		
		if (command.equals("admin_invul"))
		{
			handleInvul(activeChar);
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		else if (command.equals("admin_undying"))
		{
			handleUndying(activeChar, activeChar);
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		
		else if (command.equals("admin_setinvul"))
		{
			final WorldObject target = activeChar.getTarget();
			if ( isPlayer(target))
			{
				handleInvul((Player) target);
			}
		}
		else if (command.equals("admin_setundying"))
		{
			final WorldObject target = activeChar.getTarget();
			if (isCreature(target))
			{
				handleUndying(activeChar, (Creature) target);
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void handleInvul(Player activeChar)
	{
		String text;
		if (activeChar.isInvul())
		{
			activeChar.setIsInvul(false);
			text = activeChar.getName() + " is now mortal.";
		}
		else
		{
			activeChar.setIsInvul(true);
			text = activeChar.getName() + " is now invulnerable.";
		}
		BuilderUtil.sendSysMessage(activeChar, text);
	}
	
	private void handleUndying(Player activeChar, Creature target)
	{
		String text;
		if (target.isUndying())
		{
			target.setUndying(false);
			text = target.getName() + " is now mortal.";
		}
		else
		{
			target.setUndying(true);
			text = target.getName() + " is now undying.";
		}
		BuilderUtil.sendSysMessage(activeChar, text);
	}
}
