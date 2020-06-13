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

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Mobius
 */
public class AdminHwid implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_hwid",
		"admin_hwinfo"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (!isPlayer(activeChar.getTarget()) || (activeChar.getTarget().getActingPlayer().getClient() == null) || (activeChar.getTarget().getActingPlayer().getClient().getHardwareInfo() == null))
		{
			return true;
		}
		final Player target = activeChar.getTarget().getActingPlayer();
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/charhwinfo.htm"));
		html.replace("%name%", target.getName());
		html.replace("%macAddress%", target.getClient().getHardwareInfo().getMacAddress());
		html.replace("%windowsPlatformId%", target.getClient().getHardwareInfo().getWindowsPlatformId());
		html.replace("%windowsMajorVersion%", target.getClient().getHardwareInfo().getWindowsMajorVersion());
		html.replace("%windowsMinorVersion%", target.getClient().getHardwareInfo().getWindowsMinorVersion());
		html.replace("%windowsBuildNumber%", target.getClient().getHardwareInfo().getWindowsBuildNumber());
		html.replace("%cpuName%", target.getClient().getHardwareInfo().getCpuName());
		html.replace("%cpuSpeed%", target.getClient().getHardwareInfo().getCpuSpeed());
		html.replace("%cpuCoreCount%", target.getClient().getHardwareInfo().getCpuCoreCount());
		html.replace("%vgaName%", target.getClient().getHardwareInfo().getVgaName());
		html.replace("%vgaDriverVersion%", target.getClient().getHardwareInfo().getVgaDriverVersion());
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}