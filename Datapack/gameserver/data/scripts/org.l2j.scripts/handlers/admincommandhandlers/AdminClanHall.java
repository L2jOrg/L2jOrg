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

import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BypassParser;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Clan Hall admin commands.
 * @author St3eT
 */
public final class AdminClanHall implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_clanhall",
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		if (actualCommand.toLowerCase().equals("admin_clanhall"))
		{
			processBypass(activeChar, new BypassParser(command));
		}
		return true;
	}
	
	private void doAction(Player player, int clanHallId, String action, String actionVal)
	{
		final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
		if (clanHall != null)
		{
			switch (action)
			{
				case "openCloseDoors":
				{
					if (actionVal != null)
					{
						clanHall.openCloseDoors(Boolean.parseBoolean(actionVal));
					}
					break;
				}
				case "teleport":
				{
					if (actionVal != null)
					{
						final Location loc;
						switch (actionVal)
						{
							case "inside":
							{
								loc = clanHall.getOwnerLocation();
								break;
							}
							case "outside":
							{
								loc = clanHall.getBanishLocation();
								break;
							}
							default:
							{
								loc = player.getLocation();
							}
						}
						player.teleToLocation(loc);
					}
					break;
				}
				case "give":
				{
					if ((player.getTarget() != null) && (player.getTarget().getActingPlayer() != null))
					{
						final Clan targetClan = player.getTarget().getActingPlayer().getClan();
						if ((targetClan == null) || (targetClan.getHideoutId() != 0))
						{
							player.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
						}
						
						clanHall.setOwner(targetClan);
					}
					else
					{
						player.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
					}
					break;
				}
				case "take":
				{
					final Clan clan = clanHall.getOwner();
					if (clan != null)
					{
						clanHall.setOwner(null);
					}
					else
					{
						player.sendMessage("You cannot take Clan Hall which don't have any owner.");
					}
					break;
				}
				case "cancelFunc":
				{
					final ResidenceFunctionData function = clanHall.getFunction(Integer.parseInt(actionVal));
					if (function != null)
					{
						clanHall.removeFunction(function);
						sendClanHallDetails(player, clanHallId);
					}
					break;
				}
			}
		}
		else
		{
			player.sendMessage("Clan Hall with id " + clanHallId + " does not exist!");
		}
		useAdminCommand("admin_clanhall id=" + clanHallId, player);
	}
	
	private void sendClanHallList(Player player, int page, BypassParser parser)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(player, "data/html/admin/clanhall_list.htm");
		final List<ClanHall> clanHallList = ClanHallManager.getInstance().getClanHalls().stream().sorted(Comparator.comparingLong(ClanHall::getId)).collect(Collectors.toList());
		
		//@formatter:off
		final PageResult result = PageBuilder.newBuilder(clanHallList, 4, "bypass -h admin_clanhall")
			.currentPage(page)
			.pageHandler(NextPrevPageHandler.INSTANCE)
			.formatter(BypassParserFormatter.INSTANCE)
			.style(ButtonsStyle.INSTANCE)
			.bodyHandler((pages, clanHall, sb) ->
		{
			sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
			sb.append("<tr><td align=center fixwidth=\"250\"><font color=\"LEVEL\">&%" + clanHall.getId() + "; (" + clanHall.getId() + ")</font></td></tr>");
			sb.append("</table>");

			sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
			sb.append("<tr>");		
			sb.append("<td align=center fixwidth=\"83\">Status:</td>");		
			sb.append("<td align=center fixwidth=\"83\"></td>");		
			sb.append("<td align=center fixwidth=\"83\">" + (clanHall.getOwner() == null ? "<font color=\"00FF00\">Free</font>" : "<font color=\"FF9900\">Owned</font>") + "</td>");		
			sb.append("</tr>");
			
			sb.append("<tr>");
			sb.append("<td align=center fixwidth=\"83\">Location:</td>");
			sb.append("<td align=center fixwidth=\"83\"></td>");
			sb.append("<td align=center fixwidth=\"83\">&^" + clanHall.getId() + ";</td>");
			sb.append("</tr>");
			
			sb.append("<tr>");
			sb.append("<td align=center fixwidth=\"83\">Detailed Info:</td>");
			sb.append("<td align=center fixwidth=\"83\"></td>");
			sb.append("<td align=center fixwidth=\"83\"><button value=\"Show me!\" action=\"bypass -h admin_clanhall id=" + clanHall.getId() + "\" width=\"85\" height=\"20\" back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			sb.append("</tr>");
			
			
			sb.append("</table>");
			sb.append("<br>");
		}).build();
		//@formatter:on
		
		html.replace("%pages%", result.getPages() > 0 ? "<center><table width=\"100%\" cellspacing=0><tr>" + result.getPagerTemplate() + "</tr></table></center>" : "");
		html.replace("%data%", result.getBodyTemplate().toString());
		player.sendPacket(html);
	}
	
	private void sendClanHallDetails(Player player, int clanHallId)
	{
		final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
		if (clanHall != null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			final StringBuilder sb = new StringBuilder();
			html.setFile(player, "data/html/admin/clanhall_detail.htm");
			html.replace("%clanHallId%", clanHall.getId());
			html.replace("%clanHallOwner%", (clanHall.getOwner() == null ? "<font color=\"00FF00\">Free</font>" : "<font color=\"FF9900\">" + clanHall.getOwner().getName() + "</font>"));
			final String grade = clanHall.getGrade().toString().replace("GRADE_", "") + " Grade";
			html.replace("%clanHallGrade%", grade);
			html.replace("%clanHallSize%", clanHall.getGrade().getGradeValue());
			
			if (!clanHall.getFunctions().isEmpty())
			{
				sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
				sb.append("<tr>");
				sb.append("<td align=center fixwidth=\"40\"><font color=\"LEVEL\">ID</font></td>");
				sb.append("<td align=center fixwidth=\"200\"><font color=\"LEVEL\">Type</font></td>");
				sb.append("<td align=center fixwidth=\"40\"><font color=\"LEVEL\">Lvl</font></td>");
				sb.append("<td align=center fixwidth=\"200\"><font color=\"LEVEL\">End date</font></td>");
				sb.append("<td align=center fixwidth=\"100\"><font color=\"LEVEL\">Action</font></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
				clanHall.getFunctions().forEach(function ->
				{
					sb.append("<tr>");
					sb.append("<td align=center fixwidth=\"40\">" + function.getId() + "</td>");
					sb.append("<td align=center fixwidth=\"200\">" + function.getType().toString() + "</td>");
					sb.append("<td align=center fixwidth=\"40\">" + function.getLevel() + "</td>");
					sb.append("<td align=center fixwidth=\"200\">" + new SimpleDateFormat("dd/MM HH:mm").format(new Date(function.getExpiration())) + "</td>");
					sb.append("<td align=center fixwidth=\"100\"><button value=\"Remove\" action=\"bypass -h admin_clanhall id=" + clanHallId + " action=cancelFunc actionVal=" + function.getId() + "\" width=50 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
					sb.append("</tr>");
				});
				sb.append("</table>");
			}
			else
			{
				sb.append("This Clan Hall doesn't have any Function yet.");
			}
			html.replace("%functionList%", sb.toString());
			player.sendPacket(html);
		}
		else
		{
			player.sendMessage("Clan Hall with id " + clanHallId + " does not exist!");
			useAdminCommand("admin_clanhall", player);
		}
	}
	
	private void processBypass(Player player, BypassParser parser)
	{
		final int page = parser.getInt("page", 0);
		final int clanHallId = parser.getInt("id", 0);
		final String action = parser.getString("action", null);
		final String actionVal = parser.getString("actionVal", null);
		
		if ((clanHallId > 0) && (action != null))
		{
			doAction(player, clanHallId, action, actionVal);
		}
		else if (clanHallId > 0)
		{
			sendClanHallDetails(player, clanHallId);
		}
		else
		{
			sendClanHallList(player, page, parser);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}