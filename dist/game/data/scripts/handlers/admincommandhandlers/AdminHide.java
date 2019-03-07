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

import com.l2jmobius.gameserver.handler.IAdminCommandHandler;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.util.BuilderUtil;

/**
 * @author lord_rex
 */
public final class AdminHide implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hide"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		try
		{
			final String param = st.nextToken();
			switch (param)
			{
				case "on":
				{
					if (!BuilderUtil.setHiding(player, true))
					{
						BuilderUtil.sendSysMessage(player, "Currently, you cannot be seen.");
						return true;
					}
					
					BuilderUtil.sendSysMessage(player, "Now, you cannot be seen.");
					return true;
				}
				case "off":
				{
					if (!BuilderUtil.setHiding(player, false))
					{
						BuilderUtil.sendSysMessage(player, "Currently, you can be seen.");
						return true;
					}
					
					BuilderUtil.sendSysMessage(player, "Now, you can be seen.");
					return true;
				}
				default:
				{
					BuilderUtil.sendSysMessage(player, "//hide [on|off]");
					return true;
				}
			}
		}
		catch (final Exception e)
		{
			BuilderUtil.sendSysMessage(player, "//hide [on|off]");
			return true;
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
