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
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isDigit;

/**
 * Handler provides ability to override server's conditions for admin.<br>
 * Note: //setparam command uses any XML value and ignores case sensitivity.<br>
 * For best results by //setparam enable the maximum stats PcCondOverride here.
 * @author UnAfraid, Nik
 */
public class AdminPcCondOverride implements IAdminCommandHandler
{
	// private static final int SETPARAM_ORDER = 0x90;
	
	private static final String[] COMMANDS =
	{
		"admin_exceptions",
		"admin_set_exception",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command);
		if (st.hasMoreTokens())
		{
			switch (st.nextToken())
			// command
			{
				case "admin_exceptions":
				{
					final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
					msg.setFile(activeChar, "data/html/admin/cond_override.htm");
					final StringBuilder sb = new StringBuilder();
					for (PcCondOverride ex : PcCondOverride.values())
					{
						sb.append("<tr><td fixwidth=\"180\">" + ex.getDescription() + ":</td><td><a action=\"bypass -h admin_set_exception " + ex.ordinal() + "\">" + (activeChar.canOverrideCond(ex) ? "Disable" : "Enable") + "</a></td></tr>");
					}
					msg.replace("%cond_table%", sb.toString());
					activeChar.sendPacket(msg);
					break;
				}
				case "admin_set_exception":
				{
					if (st.hasMoreTokens())
					{
						final String token = st.nextToken();
						if (isDigit(token))
						{
							final PcCondOverride ex = PcCondOverride.getCondOverride(Integer.valueOf(token));
							if (ex != null)
							{
								if (activeChar.canOverrideCond(ex))
								{
									activeChar.removeOverridedCond(ex);
									BuilderUtil.sendSysMessage(activeChar, "You've disabled " + ex.getDescription());
								}
								else
								{
									activeChar.addOverrideCond(ex);
									BuilderUtil.sendSysMessage(activeChar, "You've enabled " + ex.getDescription());
								}
							}
						}
						else
						{
							switch (token)
							{
								case "enable_all":
								{
									for (PcCondOverride ex : PcCondOverride.values())
									{
										if (!activeChar.canOverrideCond(ex))
										{
											activeChar.addOverrideCond(ex);
										}
									}
									BuilderUtil.sendSysMessage(activeChar, "All condition exceptions have been enabled.");
									break;
								}
								case "disable_all":
								{
									for (PcCondOverride ex : PcCondOverride.values())
									{
										if (activeChar.canOverrideCond(ex))
										{
											activeChar.removeOverridedCond(ex);
										}
									}
									BuilderUtil.sendSysMessage(activeChar, "All condition exceptions have been disabled.");
									break;
								}
							}
						}
						useAdminCommand(COMMANDS[0], activeChar);
					}
					break;
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return COMMANDS;
	}
}
