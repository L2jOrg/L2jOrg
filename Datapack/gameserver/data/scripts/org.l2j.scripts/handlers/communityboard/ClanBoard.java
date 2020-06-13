/*
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
package handlers.communityboard;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IWriteBoardHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static org.l2j.commons.util.Util.*;

/**
 * Clan board.
 * @author Zoey76
 * @author JoeAlisson
 */
public class ClanBoard implements IWriteBoardHandler {

	private static final String[] COMMANDS = {
		"_bbsclan",
	};

	@Override
	public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player) {
		if(tokens.hasMoreTokens()) {
			parseClanAction(tokens, player);
		} else {
			if (falseIfNullOrElse(player.getClan(), c -> c.getLevel() >= 2)) {
				clanHome(player);
			} else {
				clanList(player, 1);
			}
		}
		return true;
	}

	protected void parseClanAction(StringTokenizer tokens, Player player) {
		switch (tokens.nextToken()){
			case "list" -> clanList(player, parseNextInt(tokens, 1));
			case "home" -> clanHome(player, parseNextInt(tokens, player.getClanId()));
			case "notice" -> clanNotice(player, tokens);
		}
	}

	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}

	private void clanNotice(Player player, StringTokenizer tokens) {
		if (tokens.hasMoreTokens()) {
			doIfNonNull(player.getClan(), clan -> clan.setNoticeEnabled("enable".equalsIgnoreCase(tokens.nextToken())));
		}
		clanNotice(player);
	}

	private void clanNotice(Player player) {
		doIfNonNull(player.getClan(), clan -> {
			if (clan.getLevel() < 2) {
				player.sendPacket(SystemMessageId.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER);

			} else {
				final StringBuilder html = new StringBuilder(2048);
				html.append("<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a> &gt; <a action=\"bypass _bbsclan list\"> CLAN COMMUNITY </a>  &gt; <a action=\"bypass _bbsclan home ");
				html.append(clan.getId());
				html.append("\"> &amp;$802; </a></td></tr></table>");
				if (player.isClanLeader())
				{
					html.append("<br><br><center><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td fixwidth=610><font color=\"AAAAAA\">The Clan Notice function allows the clan leader to send messages through a pop-up window to clan members at login.</font> </td></tr><tr><td height=20></td></tr>");

					if (player.getClan().isNoticeEnabled())
					{
						html.append("<tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;on&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan notice disable\">off</a>");
					}
					else
					{
						html.append("<tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan notice enable\">on</a>&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;off");
					}

					html.append("</td></tr></table><img src=\"L2UI.Squaregray\" width=\"610\" height=\"1\"><br> <br><table width=610 border=0 cellspacing=2 cellpadding=0><tr><td>Edit Notice: </td></tr><tr><td height=5></td></tr><tr><td><MultiEdit var =\"Content\" width=610 height=100></td></tr></table><br><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td height=5></td></tr><tr><td align=center FIXWIDTH=65><button value=\"&$140;\" action=\"Write _bbsclan Set _ Content Content Content\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td><td align=center FIXWIDTH=45></td><td align=center FIXWIDTH=500></td></tr></table></center></body></html>");

					GameUtils.sendCBHtml(player, html.toString(), player.getClan().getNotice());
				}
				else
				{
					html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0><tr><td>You are not your clan's leader, and therefore cannot change the clan notice</td></tr></table>");
					if (player.getClan().isNoticeEnabled())
					{
						html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td>The current clan notice:</td></tr><tr><td fixwidth=5></td><td FIXWIDTH=600 align=left>" + player.getClan().getNotice() + "</td><td fixqqwidth=5></td></tr></table>");
					}
					html.append("</center></body></html>");
					CommunityBoardHandler.separateAndSend(html.toString(), player);
				}
			}
		});
	}

	
	private void clanList(Player activeChar, int index)
	{
		if (index < 1)
		{
			index = 1;
		}
		
		// header
		final StringBuilder html = new StringBuilder(2048);
		html.append("<html><body><br><br><center><br1><br1><table border=0 cellspacing=0 cellpadding=0><tr><td FIXWIDTH=15>&nbsp;</td><td width=610 height=30 align=left><a action=\"bypass _bbsclan list\"> CLAN COMMUNITY </a></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=434343><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixWIDTH=600><a action=\"bypass _bbsclan home ");
		html.append(activeChar.getClan() != null ? activeChar.getClan().getId() : 0);
		html.append("\">[GO TO MY CLAN]</a>&nbsp;&nbsp;</td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><br><table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610><tr><td FIXWIDTH=5></td><td FIXWIDTH=200 align=center>CLAN NAME</td><td FIXWIDTH=200 align=center>CLAN LEADER</td><td FIXWIDTH=100 align=center>CLAN LEVEL</td><td FIXWIDTH=100 align=center>CLAN MEMBERS</td><td FIXWIDTH=5></td></tr></table><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");
		
		int i = 0;
		for (Clan cl : ClanTable.getInstance().getClans())
		{
			if (i > ((index + 1) * 7))
			{
				break;
			}
			
			if (i++ >= ((index - 1) * 7))
			{
				html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\"><table border=0 cellspacing=0 cellpadding=0 width=610><tr> <td FIXWIDTH=5></td><td FIXWIDTH=200 align=center><a action=\"bypass _bbsclan home ");
				html.append(cl.getId());
				html.append("\">");
				html.append(cl.getName());
				html.append("</a></td><td FIXWIDTH=200 align=center>");
				html.append(cl.getLeaderName());
				html.append("</td><td FIXWIDTH=100 align=center>");
				html.append(cl.getLevel());
				html.append("</td><td FIXWIDTH=100 align=center>");
				html.append(cl.getMembersCount());
				html.append("</td><td FIXWIDTH=5></td></tr><tr><td height=5></td></tr></table><img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\"><img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
			}
		}
		
		html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"2\"><table cellpadding=0 cellspacing=2 border=0><tr>");
		
		if (index == 1)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		else
		{
			html.append("<td><button action=\"_bbsclan list ");
			html.append(index - 1);
			html.append("\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		
		i = 0;
		int nbp = ClanTable.getInstance().getClanCount() / 8;
		if ((nbp * 8) != ClanTable.getInstance().getClanCount())
		{
			nbp++;
		}
		for (i = 1; i <= nbp; i++)
		{
			if (i == index)
			{
				html.append("<td> ");
				html.append(i);
				html.append(" </td>");
			}
			else
			{
				html.append("<td><a action=\"bypass _bbsclan list ");
				html.append(i);
				html.append("\"> ");
				html.append(i);
				html.append(" </a></td>");
			}
			
		}
		if (index == nbp)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		else
		{
			html.append("<td><button action=\"bypass _bbsclan list ");
			html.append(index + 1);
			html.append("\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		html.append("</tr></table><table border=0 cellspacing=0 cellpadding=0><tr><td width=610><img src=\"sek.cbui141\" width=\"610\" height=\"1\"></td></tr></table><table border=0><tr><td><combobox width=65 var=keyword list=\"Name;Ruler\"></td><td><edit var = \"Search\" width=130 height=11 length=\"16\"></td>" +
		// TODO: search (Write in BBS)
			"<td><button value=\"&$420;\" action=\"Write 5 -1 0 Search keyword keyword\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td> </tr></table><br><br></center></body></html>");
		CommunityBoardHandler.separateAndSend(html.toString(), activeChar);
	}
	
	private void clanHome(Player activeChar) {
		clanHome(activeChar, activeChar.getClan().getId());
	}
	
	private void clanHome(Player activeChar, int clanId)
	{
		final Clan cl = ClanTable.getInstance().getClan(clanId);
		if (cl != null)
		{
			if (cl.getLevel() < 2)
			{
				activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER);
			}
			else
			{
				final String html = Arrays.asList("<html><body><center><br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0><tr><td FIXWIDTH=15>&nbsp;</td><td width=610 height=30 align=left><a action=\"bypass _bbshome\">HOME</a> &gt; <a action=\"bypass _bbsclan list\"> CLAN COMMUNITY </a>  &gt; <a action=\"bypass _bbsclan home ", String.valueOf(clanId), "\"> &amp;$802; </a></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=434343><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixwidth=600><a action=\"bypass _bbsclan home ", String.valueOf(clanId), ";announce\">[CLAN ANNOUNCEMENT]</a> <a action=\"bypass _bbsclan home ", String.valueOf(clanId), "\">[CLAN BULLETIN BOARD]</a><a action=\"bypass _bbsclan home ", String.valueOf(clanId), ";cmail\">[CLAN MAIL]</a>&nbsp;&nbsp;<a action=\"bypass _bbsclan notice\">[CLAN NOTICE]</a>&nbsp;&nbsp;</td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixwidth=290 valign=top></td><td fixWIDTH=5></td><td fixWIDTH=5 align=center valign=top><img src=\"l2ui.squaregray\" width=2  height=128></td><td fixWIDTH=5></td><td fixwidth=295><table border=0 cellspacing=0 cellpadding=0 width=295><tr><td fixWIDTH=100 align=left>CLAN NAME</td><td fixWIDTH=195 align=left>", cl.getName(), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN LEVEL</td><td fixWIDTH=195 align=left height=16>", String.valueOf(cl.getLevel()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN MEMBERS</td><td fixWIDTH=195 align=left height=16>", String.valueOf(cl.getMembersCount()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN LEADER</td><td fixWIDTH=195 align=left height=16>", cl.getLeaderName(), "</td></tr><tr><td height=7></td></tr>" +
				// ADMINISTRATOR ??
				/*
				 * html.append("<tr>"); html.append("<td fixWIDTH=100 align=left>ADMINISTRATOR</td>"); html.append("<td fixWIDTH=195 align=left height=16>"+cl.getLeaderName()+"</td>"); html.append("</tr>");
				 */
					"<tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>ALLIANCE</td><td fixWIDTH=195 align=left height=16>", (cl.getAllyName() != null) ? cl.getAllyName() : "", "</td></tr></table></td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table>" +
				// TODO: the BB for clan :)
				// html.append("<table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=333333>");
						"<img src=\"L2UI.squareblank\" width=\"1\" height=\"5\"><img src=\"L2UI.squaregray\" width=\"610\" height=\"1\"><br></center><br> <br></body></html>").stream().collect(Collectors.joining());
				CommunityBoardHandler.separateAndSend(html, activeChar);
			}
		}
	}
	
	@Override
	public boolean writeCommunityBoardCommand(Player activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		// the only Write bypass that comes to this handler is "Write Notice Set _ Content Content Content";
		// arg1 = Set, arg2 = _
		final Clan clan = activeChar.getClan();
		
		if ((clan != null) && activeChar.isClanLeader())
		{
			clan.setNotice(arg3);
		}
		
		return true;
	}
}
