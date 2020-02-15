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

import java.util.StringTokenizer;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The classical custom L2J implementation of the old //gmspeed GM command.
 * @author lord_rex (No, it wasn't me at all. Eclipse added my name there.)
 */
public final class AdminSuperHaste implements IAdminCommandHandler
{
	static final String[] ADMIN_COMMANDS =
	{
		"admin_superhaste",
		"admin_superhaste_menu",
		"admin_speed",
		"admin_speed_menu",
	};
	
	private static final int SUPER_HASTE_ID = 7029;
	
	@Override
	public boolean useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		final String cmd = st.nextToken();
		switch (cmd)
		{
			case "admin_superhaste":
			case "admin_speed":
			{
				try
				{
					final int val = Integer.parseInt(st.nextToken());
					final boolean sendMessage = player.isAffectedBySkill(SUPER_HASTE_ID);
					player.stopSkillEffects((val == 0) && sendMessage, SUPER_HASTE_ID);
					if ((val >= 1) && (val <= 4))
					{
						int time = 0;
						if (st.hasMoreTokens())
						{
							time = Integer.parseInt(st.nextToken());
						}
						
						final Skill superHasteSkill = SkillEngine.getInstance().getSkill(SUPER_HASTE_ID, val);
						superHasteSkill.applyEffects(player, player, true, time);
					}
				}
				catch (final Exception e)
				{
					player.sendMessage("Usage: //superhaste <Effect level (0-4)> <Time in seconds>");
				}
				
				break;
			}
			case "admin_superhaste_menu":
			case "admin_speed_menu":
			{
				AdminHtml.showAdminHtml(player, "gm_menu.htm");
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
