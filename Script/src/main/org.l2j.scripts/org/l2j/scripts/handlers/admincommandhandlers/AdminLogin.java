/*
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

import org.l2j.gameserver.ServerType;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.AdminSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * This class handles the admin commands that acts on the login
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2007/07/31 10:05:56 $
 */
public class AdminLogin implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_server_gm_only",
		"admin_server_all",
		"admin_server_max_player",
		"admin_server_list_age",
		"admin_server_login"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_server_gm_only"))
		{
			gmOnly();
			BuilderUtil.sendSysMessage(activeChar, "Server is now GM only");
			showMainPage(activeChar);
		}
		else if (command.equals("admin_server_all"))
		{
			allowToAll();
			BuilderUtil.sendSysMessage(activeChar, "Server is not GM only anymore");
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_server_max_player"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				final String number = st.nextToken();
				try
				{
					//TODO Implement AuthServerCommunication.getInstance().setMaxPlayer(Integer.parseInt(number));
					BuilderUtil.sendSysMessage(activeChar, "maxPlayer set to " + number);
					showMainPage(activeChar);
				}
				catch (NumberFormatException e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Max players must be a number.");
				}
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Format is server_max_player <max>");
			}
		}
		else if (command.startsWith("admin_server_list_age"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				final String mode = st.nextToken();
				int age;
				try
				{
					age = Integer.parseInt(mode);
					if (ServerSettings.ageLimit() != age)
					{
						ServerSettings.setAgeLimit((byte) age);
						// TODO Implement AuthServerCommunication.getInstance().sendServerStatus(ServerStatus.SERVER_AGE, age);
						BuilderUtil.sendSysMessage(activeChar, "Server Age changed to " + age);
						showMainPage(activeChar);
					}
					else
					{
						BuilderUtil.sendSysMessage(activeChar, "Server Age is already " + age);
						showMainPage(activeChar);
					}
				}
				catch (NumberFormatException e)
				{
					BuilderUtil.sendSysMessage(activeChar, "Age must be a number");
				}
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Format is server_list_age <number>");
			}
		}
		else if (command.equals("admin_server_login"))
		{
			showMainPage(activeChar);
		}
		return true;
	}

	private void showMainPage(Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/login.htm");
		// TODO Implement html.replace("%server_name%", AuthServerCommunication.getInstance().getServerName());
		// TODO Implment html.replace("%status%", AuthServerCommunication.getInstance().getStatusString());
		html.replace("%type%", getServerTypeName(ServerSettings.type()));
		html.replace("%brackets%", String.valueOf(ServerSettings.isShowingBrackets()));
		// TODO implement html.replace("%max_players%", String.valueOf(AuthServerCommunication.getInstance().getMaxPlayer()));
		activeChar.sendPacket(html);
	}
	
	private String getServerTypeName(int serverType) {
		return Arrays.stream(ServerType.values()).filter(type -> (serverType & type.getMask()) != 0).map(ServerType::toString).collect(Collectors.joining(", "));
	}

	private void allowToAll() {
		AdminSettings.gmOnlyServer(false);
		// TODO Implement AuthServerCommunication.getInstance().setServerStatus(ServerStatus.STATUS_AUTO);
	}

	private void gmOnly() {
		// TODO  IMPLEMENT AuthServerCommunication.getInstance().setServerStatus(ServerStatus.STATUS_GM_ONLY);
		AdminSettings.gmOnlyServer(true);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
