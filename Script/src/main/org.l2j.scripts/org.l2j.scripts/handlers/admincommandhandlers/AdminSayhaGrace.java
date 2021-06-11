/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Psychokiller1888
 */
public class AdminSayhaGrace implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
			"admin_set_sayha_grace",
			"admin_full_sayha_grace",
			"admin_empty_sayha_grace",
			"admin_get_sayha_grace"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (activeChar == null)
		{
			return false;
		}
		
		if (CharacterSettings.isSayhaGraceEnabled())
		{
			BuilderUtil.sendSysMessage(activeChar, "Sayha is not enabled on the server!");
			return false;
		}

		int sayha = 0;
		
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String cmd = st.nextToken();
		
		if (isPlayer(activeChar.getTarget()))
		{
			final Player target = (Player) activeChar.getTarget();
			
			if (cmd.equals("admin_set_sayha_grace"))
			{
				try
				{
					sayha = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Incorrect sayha");
				}
				
				target.setSayhaGracePoints(sayha, true);
				target.sendMessage("Admin set your SayhaGrace points to " + sayha);
			}
			else if (cmd.equals("admin_full_sayha_grace"))
			{
				target.setSayhaGracePoints(PlayerStats.MAX_SAYHA_GRACE_POINTS, true);
				target.sendMessage("Admin completly recharged your Vitality");
			}
			else if (cmd.equals("admin_empty_sayha_grace"))
			{
				target.setSayhaGracePoints(PlayerStats.MIN_SAYHA_GRACE_POINTS, true);
				target.sendMessage("Admin completly emptied your Vitality");
			}
			else if (cmd.equals("admin_get_sayha_grace"))
			{
				sayha = target.getSayhaGracePoints();
				BuilderUtil.sendSysMessage(activeChar, "Player vitality points: " + sayha);
			}
			return true;
		}
		BuilderUtil.sendSysMessage(activeChar, "Target not found or not a player");
		return false;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
