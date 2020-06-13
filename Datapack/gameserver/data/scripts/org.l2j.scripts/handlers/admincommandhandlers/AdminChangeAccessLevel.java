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

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Change access level command handler.
 */
public final class AdminChangeAccessLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_changelvl"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player gm)
	{
		final String[] parts = command.split(" ");
		if (parts.length == 2)
		{
			try
			{
				final int lvl = Integer.parseInt(parts[1]);
				final WorldObject target = gm.getTarget();
				if (!isPlayer(target))
				{
					gm.sendPacket(SystemMessageId.INVALID_TARGET);
				}
				else
				{
					onlineChange(gm, (Player) target, lvl);
				}
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(gm, "Usage: //changelvl <target_new_level> | <player_name> <new_level>");
			}
		}
		else if (parts.length == 3)
		{
			final String name = parts[1];
			final int level = Integer.parseInt(parts[2]);
			final Player player = World.getInstance().findPlayer(name);
			if (player != null)
			{
				onlineChange(gm, player, level);
			}
			else {
				if(getDAO(PlayerDAO.class).updateAccessLevelByName(name, level)) {
					BuilderUtil.sendSysMessage(gm, "Character's access level is now set to " + level);
				} else {
					BuilderUtil.sendSysMessage(gm, "Character not found or access level unaltered.");
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * @param activeChar the active GM
	 * @param player the online target
	 * @param lvl the access level
	 */
	private static void onlineChange(Player activeChar, Player player, int lvl)
	{
		if (lvl >= 0)
		{
			final AccessLevel acccessLevel = AdminData.getInstance().getAccessLevel(lvl);
			if (acccessLevel != null)
			{
				player.setAccessLevel(lvl, true, true);
				player.sendMessage("Your access level has been changed to " + acccessLevel.getName() + " (" + acccessLevel.getLevel() + ").");
				activeChar.sendMessage(player.getName() + "'s access level has been changed to " + acccessLevel.getName() + " (" + acccessLevel.getLevel() + ").");
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "You are trying to set unexisting access level: " + lvl + " please try again with a valid one!");
			}
		}
		else
		{
			player.setAccessLevel(lvl, false, true);
			player.sendMessage("Your character has been banned. Bye.");
			Disconnection.of(player).defaultSequence(false);
		}
	}
}
