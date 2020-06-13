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

import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author St3eT
 */
public class AdminServerInfo implements IAdminCommandHandler
{
	private static final SimpleDateFormat fmt = new SimpleDateFormat("hh:mm a");
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_serverinfo"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_serverinfo"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage();
			final Runtime RunTime = Runtime.getRuntime();
			final int mb = 1024 * 1024;
			html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/serverinfo.htm"));
			
			html.replace("%os_name%", System.getProperty("os.name"));
			html.replace("%os_ver%", System.getProperty("os.version"));
			html.replace("%slots%", getPlayersCount("ALL") + "/" + getSettings(ServerSettings.class).maximumOnlineUsers());
			html.replace("%gameTime%", WorldTimeController.getInstance().getGameHour() + ":" + WorldTimeController.getInstance().getGameMinute());
			html.replace("%dayNight%", WorldTimeController.getInstance().isNight() ? "Night" : "Day");
			html.replace("%geodata%", getSettings(GeoEngineSettings.class).isEnabledPathFinding() ? "Enabled" : "Disabled");
			html.replace("%serverTime%", fmt.format(new Date(System.currentTimeMillis())));
			html.replace("%serverUpTime%", GameServer.getInstance().getUptime());
			html.replace("%onlineAll%", getPlayersCount("ALL"));
			html.replace("%offlineTrade%", getPlayersCount("OFF_TRADE"));
			html.replace("%onlineGM%", getPlayersCount("GM"));
			html.replace("%onlineReal%", getPlayersCount("ALL_REAL"));
			html.replace("%usedMem%", (RunTime.maxMemory() / mb) - (((RunTime.maxMemory() - RunTime.totalMemory()) + RunTime.freeMemory()) / mb));
			html.replace("%freeMem%", ((RunTime.maxMemory() - RunTime.totalMemory()) + RunTime.freeMemory()) / mb);
			html.replace("%totalMem%", Runtime.getRuntime().maxMemory() / 1048576);
			activeChar.sendPacket(html);
		}
		return true;
	}

	private int getPlayersCount(String type)
	{
		switch (type)
		{
			case "ALL":
			{
				return World.getInstance().getPlayers().size();
			}
			case "OFF_TRADE":
			{
				int offlineCount = 0;
				
				final Collection<Player> objs = World.getInstance().getPlayers();
				for (Player player : objs)
				{
					if ((player.getClient() == null) || player.getClient().isDetached())
					{
						offlineCount++;
					}
				}
				return offlineCount;
			}
			case "GM":
			{
				int onlineGMcount = 0;
				for (Player gm : AdminData.getInstance().getAllGms(true))
				{
					if ((gm != null) && gm.isOnline() && (gm.getClient() != null) && !gm.getClient().isDetached())
					{
						onlineGMcount++;
					}
				}
				return onlineGMcount;
			}
			case "ALL_REAL":
			{
				final Set<String> realPlayers = new HashSet<>();
				
				for (Player onlinePlayer : World.getInstance().getPlayers())
				{
					if ((onlinePlayer != null) && (onlinePlayer.getClient() != null) && !onlinePlayer.getClient().isDetached())
					{
						realPlayers.add(onlinePlayer.getIPAddress());
					}
				}
				return realPlayers.size();
			}
		}
		return 0;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
