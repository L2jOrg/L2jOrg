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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

import java.util.StringTokenizer;

/**
 * Admin PC Points manage admin commands.
 */
public final class AdminPcCafePoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_pccafepoints",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.equals("admin_pccafepoints"))
		{
			if (st.hasMoreTokens())
			{
				final String action = st.nextToken();
				
				final Player target = getTarget(activeChar);
				if ((target == null) || !st.hasMoreTokens())
				{
					return false;
				}
				
				int value;
				try
				{
					value = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					showMenuHtml(activeChar);
					BuilderUtil.sendSysMessage(activeChar, "Invalid Value!");
					return false;
				}
				
				switch (action)
				{
					case "set":
					{
						if (value > Config.PC_CAFE_MAX_POINTS)
						{
							showMenuHtml(activeChar);
							BuilderUtil.sendSysMessage(activeChar, "You cannot set more than " + Config.PC_CAFE_MAX_POINTS + " PC points!");
							return false;
						}
						if (value < 0)
						{
							value = 0;
						}
						
						target.setPcCafePoints(value);
						target.sendMessage("Admin set your PC Cafe point(s) to " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You set " + value + " PC Cafe point(s) to player " + target.getName());
						target.sendPacket(new ExPCCafePointInfo(value, value, 1));
						break;
					}
					case "increase":
					{
						if (target.getPcCafePoints() == Config.PC_CAFE_MAX_POINTS)
						{
							showMenuHtml(activeChar);
							activeChar.sendMessage(target.getName() + " already have max count of PC points!");
							return false;
						}
						
						int pcCafeCount = Math.min(target.getPcCafePoints() + value, Config.PC_CAFE_MAX_POINTS);
						if (pcCafeCount < 0)
						{
							pcCafeCount = Config.PC_CAFE_MAX_POINTS;
						}
						target.setPcCafePoints(pcCafeCount);
						target.sendMessage("Admin increased your PC Cafe point(s) by " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You increased PC Cafe point(s) of " + target.getName() + " by " + value);
						target.sendPacket(new ExPCCafePointInfo(pcCafeCount, value, 1));
						break;
					}
					case "decrease":
					{
						if (target.getPcCafePoints() == 0)
						{
							showMenuHtml(activeChar);
							activeChar.sendMessage(target.getName() + " already have min count of PC points!");
							return false;
						}
						
						final int pcCafeCount = Math.max(target.getPcCafePoints() - value, 0);
						target.setPcCafePoints(pcCafeCount);
						target.sendMessage("Admin decreased your PC Cafe point(s) by " + value + "!");
						BuilderUtil.sendSysMessage(activeChar, "You decreased PC Cafe point(s) of " + target.getName() + " by " + value);
						target.sendPacket(new ExPCCafePointInfo(target.getPcCafePoints(), -value, 1));
						break;
					}
					case "rewardOnline":
					{
						int range = 0;
						try
						{
							range = Integer.parseInt(st.nextToken());
						}
						catch (Exception ignored)
						{
						}

						final var points = value;
						
						if (range <= 0)
						{
							World.getInstance().getPlayers().forEach(player -> increasePoints(player, points));
							BuilderUtil.sendSysMessage(activeChar, "You increased PC Cafe point(s) of all online players by " + value + ".");
						}
						else {
							World.getInstance().forEachPlayerInRange(activeChar, range, player -> increasePoints(player, points), this::canReceivePoints);
							BuilderUtil.sendSysMessage(activeChar, "You increased PC Cafe point(s) of all players  in range " + range + " by " + value + ".");
						}
						break;
					}
				}
			}
			showMenuHtml(activeChar);
		}
		return true;
	}

	private boolean canReceivePoints(Player player) {
		return player.isOnlineInt() == 1 &&  player.getPcCafePoints() < Integer.MAX_VALUE;
	}

	private void increasePoints(Player player, int points) {
		int pcCafeCount = Math.min(player.getPcCafePoints() + points, Integer.MAX_VALUE);
		if (pcCafeCount < 0)
		{
			pcCafeCount = Integer.MAX_VALUE;
		}
		player.setPcCafePoints(pcCafeCount);
		player.sendMessage("Admin increased your PC Cafe point(s) by " + points + "!");
		player.sendPacket(new ExPCCafePointInfo(pcCafeCount, points, 1));
	}

	
	private Player getTarget(Player activeChar)
	{
		return ((activeChar.getTarget() != null) && (activeChar.getTarget().getActingPlayer() != null)) ? activeChar.getTarget().getActingPlayer() : activeChar;
	}
	
	private void showMenuHtml(Player activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		final Player target = getTarget(activeChar);
		final int points = target.getPcCafePoints();
		html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/pccafe.htm"));
		html.replace("%points%", GameUtils.formatAdena(points));
		html.replace("%targetName%", target.getName());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}