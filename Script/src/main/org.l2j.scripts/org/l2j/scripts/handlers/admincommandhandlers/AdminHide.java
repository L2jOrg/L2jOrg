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

import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.StringTokenizer;

/**
 * @author lord_rex
 * @author JoeAlisson
 */
public final class AdminHide implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = {
		"admin_hide"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		try
		{
			final String param = st.nextToken();
			switch (param) {
				case "on" -> hide(player);
				case "off" -> unHide(player);
				default -> BuilderUtil.sendSysMessage(player, "//hide [on|off]");
			}
			return true;
		}
		catch (final Exception e) {
			BuilderUtil.sendSysMessage(player, "//hide [on|off]");
			return false;
		}
	}

	private void unHide(Player player) {
		if (!BuilderUtil.setHiding(player, false)) {
			BuilderUtil.sendSysMessage(player, "Currently, you can be seen.");
		} else {
			BuilderUtil.sendSysMessage(player, "Now, you can be seen.");
		}
	}

	private void hide(Player player) {
		if (!BuilderUtil.setHiding(player, true)) {
			BuilderUtil.sendSysMessage(player, "Currently, you cannot be seen.");
		} else {
			BuilderUtil.sendSysMessage(player, "Now, you cannot be seen.");
		}
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
