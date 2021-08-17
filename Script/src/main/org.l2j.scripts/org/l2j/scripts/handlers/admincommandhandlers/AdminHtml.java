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

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.Objects;
import java.util.StringTokenizer;

/**
 * @author NosBit
 */
public class AdminHtml implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_html",
		"admin_loadhtml"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		if ("admin_html".equals(actualCommand.toLowerCase())) {
			if (!st.hasMoreTokens()) {
				BuilderUtil.sendSysMessage(activeChar, "Usage: //html path");
				return false;
			}

			final String path = st.nextToken();
			showAdminHtml(activeChar, path);
		} else if ("admin_loadhtml".equals(actualCommand.toLowerCase())) {
			if (!st.hasMoreTokens()) {
				BuilderUtil.sendSysMessage(activeChar, "Usage: //loadhtml path");
				return false;
			}

			final String path = st.nextToken();
			showHtml(activeChar, path, true);
		}
		return true;
	}
	
	/**
	 * Shows a html message to activeChar
	 * @param activeChar activeChar where html is shown
	 * @param path relative path from directory data/html/admin/ to html
	 */
	static void showAdminHtml(Player activeChar, String path) {
		showHtml(activeChar, "data/html/admin/" + path, false);
	}
	
	/**
	 * Shows a html message to activeChar.
	 * @param activeChar activeChar where html message is shown.
	 * @param path relative path from Config.DATAPACK_ROOT to html.
	 * @param reload {@code true} will reload html and show it {@code false} will show it from cache.
	 */
	private static void showHtml(Player activeChar, String path, boolean reload)
	{
		String content;
		if (!reload)
		{
			content = HtmCache.getInstance().getHtm(activeChar, path);
		}
		else
		{
			content = HtmCache.getInstance().getHtm(null, path);
		}
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setHtml(Objects.requireNonNullElseGet(content, () -> "<html><body>My text is missing:<br>" + path + "</body></html>"));
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		
		return ADMIN_COMMANDS;
	}
	
}
