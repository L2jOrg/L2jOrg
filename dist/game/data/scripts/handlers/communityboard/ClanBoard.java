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
package handlers.communityboard;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.l2jmobius.gameserver.data.sql.impl.ClanTable;
import com.l2jmobius.gameserver.handler.CommunityBoardHandler;
import com.l2jmobius.gameserver.handler.IWriteBoardHandler;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.util.Util;

/**
 * Clan board.
 * @author Zoey76
 */
public class ClanBoard implements IWriteBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_bbsclan",
		"_bbsclan_list",
		"_bbsclan_clanhome"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbsclan"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Clan", command);
			
			if ((activeChar.getClan() == null) || (activeChar.getClan().getLevel() < 2))
			{
				clanList(activeChar, 1);
			}
			else
			{
				clanHome(activeChar);
			}
		}
		else if (command.startsWith("_bbsclan_clanlist"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Clan List", command);
			
			if (command.equals("_bbsclan_clanlist"))
			{
				clanList(activeChar, 1);
			}
			else if (command.startsWith("_bbsclan_clanlist;"))
			{
				try
				{
					clanList(activeChar, Integer.parseInt(command.split(";")[1]));
				}
				catch (Exception e)
				{
					clanList(activeChar, 1);
					LOG.warning(ClanBoard.class.getSimpleName() + ": Player " + activeChar + " send invalid clan list bypass " + command + "!");
				}
			}
		}
		else if (command.startsWith("_bbsclan_clanhome"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Clan Home", command);
			
			if (command.equals("_bbsclan_clanhome"))
			{
				clanHome(activeChar);
			}
			else if (command.startsWith("_bbsclan_clanhome;"))
			{
				try
				{
					clanHome(activeChar, Integer.parseInt(command.split(";")[1]));
				}
				catch (Exception e)
				{
					clanHome(activeChar);
					LOG.warning(ClanBoard.class.getSimpleName() + ": Player " + activeChar + " send invalid clan home bypass " + command + "!");
				}
			}
		}
		else if (command.startsWith("_bbsclan_clannotice_edit;"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Clan Edit", command);
			
			clanNotice(activeChar, activeChar.getClanId());
		}
		else if (command.startsWith("_bbsclan_clannotice_enable"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Clan Notice Enable", command);
			
			if (activeChar.getClan() != null)
			{
				activeChar.getClan().setNoticeEnabled(true);
			}
			clanNotice(activeChar, activeChar.getClanId());
		}
		else if (command.startsWith("_bbsclan_clannotice_disable"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Clan Notice Disable", command);
			
			if (activeChar.getClan() != null)
			{
				activeChar.getClan().setNoticeEnabled(false);
			}
			clanNotice(activeChar, activeChar.getClanId());
		}
		else
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>Command " + command + " need development.</center><br><br></body></html>", activeChar);
		}
		return true;
	}
	
	private void clanNotice(L2PcInstance activeChar, int clanId)
	{
		final L2Clan cl = ClanTable.getInstance().getClan(clanId);
		if (cl != null)
		{
			if (cl.getLevel() < 2)
			{
				activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER);
				parseCommunityBoardCommand("_bbsclan_clanlist", activeChar);
			}
			else
			{
				final StringBuilder html = new StringBuilder(2048);
				html.append("<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a> &gt; <a action=\"bypass _bbsclan_clanlist\"> CLAN COMMUNITY </a>  &gt; <a action=\"bypass _bbsclan_clanhome;");
				html.append(clanId);
				html.append("\"> &amp;$802; </a></td></tr></table>");
				if (activeChar.isClanLeader())
				{
					html.append("<br><br><center><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td fixwidth=610><font color=\"AAAAAA\">The Clan Notice function allows the clan leader to send messages through a pop-up window to clan members at login.</font> </td></tr><tr><td height=20></td></tr>");
					
					if (activeChar.getClan().isNoticeEnabled())
					{
						html.append("<tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;on&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan_clannotice_disable\">off</a>");
					}
					else
					{
						html.append("<tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan_clannotice_enable\">on</a>&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;off");
					}
					
					html.append("</td></tr></table><img src=\"L2UI.Squaregray\" width=\"610\" height=\"1\"><br> <br><table width=610 border=0 cellspacing=2 cellpadding=0><tr><td>Edit Notice: </td></tr><tr><td height=5></td></tr><tr><td><MultiEdit var =\"Content\" width=610 height=100></td></tr></table><br><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td height=5></td></tr><tr><td align=center FIXWIDTH=65><button value=\"&$140;\" action=\"Write Notice Set _ Content Content Content\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td><td align=center FIXWIDTH=45></td><td align=center FIXWIDTH=500></td></tr></table></center></body></html>");
					
					Util.sendCBHtml(activeChar, html.toString(), activeChar.getClan().getNotice());
				}
				else
				{
					html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0><tr><td>You are not your clan's leader, and therefore cannot change the clan notice</td></tr></table>");
					if (activeChar.getClan().isNoticeEnabled())
					{
						html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td>The current clan notice:</td></tr><tr><td fixwidth=5></td><td FIXWIDTH=600 align=left>" + activeChar.getClan().getNotice() + "</td><td fixqqwidth=5></td></tr></table>");
					}
					html.append("</center></body></html>");
					CommunityBoardHandler.separateAndSend(html.toString(), activeChar);
				}
			}
		}
	}
	
	private void clanList(L2PcInstance activeChar, int index)
	{
		if (index < 1)
		{
			index = 1;
		}
		
		// header
		final StringBuilder html = new StringBuilder(2048);
		html.append("<html><body><br><br><center><br1><br1><table border=0 cellspacing=0 cellpadding=0><tr><td FIXWIDTH=15>&nbsp;</td><td width=610 height=30 align=left><a action=\"bypass _bbsclan_clanlist\"> CLAN COMMUNITY </a></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=434343><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixWIDTH=600><a action=\"bypass _bbsclan_clanhome;");
		html.append(activeChar.getClan() != null ? activeChar.getClan().getId() : 0);
		html.append("\">[GO TO MY CLAN]</a>&nbsp;&nbsp;</td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><br><table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610><tr><td FIXWIDTH=5></td><td FIXWIDTH=200 align=center>CLAN NAME</td><td FIXWIDTH=200 align=center>CLAN LEADER</td><td FIXWIDTH=100 align=center>CLAN LEVEL</td><td FIXWIDTH=100 align=center>CLAN MEMBERS</td><td FIXWIDTH=5></td></tr></table><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");
		
		int i = 0;
		for (L2Clan cl : ClanTable.getInstance().getClans())
		{
			if (i > ((index + 1) * 7))
			{
				break;
			}
			
			if (i++ >= ((index - 1) * 7))
			{
				html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\"><table border=0 cellspacing=0 cellpadding=0 width=610><tr> <td FIXWIDTH=5></td><td FIXWIDTH=200 align=center><a action=\"bypass _bbsclan_clanhome;");
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
			html.append("<td><button action=\"_bbsclan_clanlist;");
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
				html.append("<td><a action=\"bypass _bbsclan_clanlist;");
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
			html.append("<td><button action=\"bypass _bbsclan_clanlist;");
			html.append(index + 1);
			html.append("\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		html.append("</tr></table><table border=0 cellspacing=0 cellpadding=0><tr><td width=610><img src=\"sek.cbui141\" width=\"610\" height=\"1\"></td></tr></table><table border=0><tr><td><combobox width=65 var=keyword list=\"Name;Ruler\"></td><td><edit var = \"Search\" width=130 height=11 length=\"16\"></td>" +
		// TODO: search (Write in BBS)
			"<td><button value=\"&$420;\" action=\"Write 5 -1 0 Search keyword keyword\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td> </tr></table><br><br></center></body></html>");
		CommunityBoardHandler.separateAndSend(html.toString(), activeChar);
	}
	
	private void clanHome(L2PcInstance activeChar)
	{
		clanHome(activeChar, activeChar.getClan().getId());
	}
	
	private void clanHome(L2PcInstance activeChar, int clanId)
	{
		final L2Clan cl = ClanTable.getInstance().getClan(clanId);
		if (cl != null)
		{
			if (cl.getLevel() < 2)
			{
				activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER);
				parseCommunityBoardCommand("_bbsclan_clanlist", activeChar);
			}
			else
			{
				final String html = Arrays.asList("<html><body><center><br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0><tr><td FIXWIDTH=15>&nbsp;</td><td width=610 height=30 align=left><a action=\"bypass _bbshome\">HOME</a> &gt; <a action=\"bypass _bbsclan_clanlist\"> CLAN COMMUNITY </a>  &gt; <a action=\"bypass _bbsclan_clanhome;", String.valueOf(clanId), "\"> &amp;$802; </a></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=434343><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixwidth=600><a action=\"bypass _bbsclan_clanhome;", String.valueOf(clanId), ";announce\">[CLAN ANNOUNCEMENT]</a> <a action=\"bypass _bbsclan_clanhome;", String.valueOf(clanId), ";cbb\">[CLAN BULLETIN BOARD]</a><a action=\"bypass _bbsclan_clanhome;", String.valueOf(clanId), ";cmail\">[CLAN MAIL]</a>&nbsp;&nbsp;<a action=\"bypass _bbsclan_clannotice_edit;", String.valueOf(clanId), ";cnotice\">[CLAN NOTICE]</a>&nbsp;&nbsp;</td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixwidth=290 valign=top></td><td fixWIDTH=5></td><td fixWIDTH=5 align=center valign=top><img src=\"l2ui.squaregray\" width=2  height=128></td><td fixWIDTH=5></td><td fixwidth=295><table border=0 cellspacing=0 cellpadding=0 width=295><tr><td fixWIDTH=100 align=left>CLAN NAME</td><td fixWIDTH=195 align=left>", cl.getName(), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN LEVEL</td><td fixWIDTH=195 align=left height=16>", String.valueOf(cl.getLevel()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN MEMBERS</td><td fixWIDTH=195 align=left height=16>", String.valueOf(cl.getMembersCount()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN LEADER</td><td fixWIDTH=195 align=left height=16>", cl.getLeaderName(), "</td></tr><tr><td height=7></td></tr>" +
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
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		// the only Write bypass that comes to this handler is "Write Notice Set _ Content Content Content";
		// arg1 = Set, arg2 = _
		final L2Clan clan = activeChar.getClan();
		
		if ((clan != null) && activeChar.isClanLeader())
		{
			clan.setNotice(arg3);
		}
		
		return true;
	}
}
