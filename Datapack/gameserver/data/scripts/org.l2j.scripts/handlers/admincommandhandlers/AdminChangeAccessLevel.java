/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final String[] parts = command.split(" ");
		if (parts.length == 2)
		{
			try
			{
				final int lvl = Integer.parseInt(parts[1]);
				final WorldObject target = activeChar.getTarget();
				if (!isPlayer(target))
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				}
				else
				{
					onlineChange(activeChar, (Player) target, lvl);
				}
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //changelvl <target_new_level> | <player_name> <new_level>");
			}
		}
		else if (parts.length == 3)
		{
			final String name = parts[1];
			final int lvl = Integer.parseInt(parts[2]);
			final Player player = World.getInstance().findPlayer(name);
			if (player != null)
			{
				onlineChange(activeChar, player, lvl);
			}
			else
			{
				try (Connection con = DatabaseFactory.getInstance().getConnection())
				{
					final PreparedStatement statement = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?");
					statement.setInt(1, lvl);
					statement.setString(2, name);
					statement.execute();
					final int count = statement.getUpdateCount();
					statement.close();
					if (count == 0)
					{
						BuilderUtil.sendSysMessage(activeChar, "Character not found or access level unaltered.");
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "Character's access level is now set to " + lvl);
					}
				}
				catch (SQLException se)
				{
					BuilderUtil.sendSysMessage(activeChar, "SQLException while changing character's access level");
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
