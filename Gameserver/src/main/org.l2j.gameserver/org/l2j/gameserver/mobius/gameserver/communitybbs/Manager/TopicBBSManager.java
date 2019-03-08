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
package org.l2j.gameserver.mobius.gameserver.communitybbs.Manager;

import org.l2j.gameserver.mobius.gameserver.communitybbs.BB.Forum;
import org.l2j.gameserver.mobius.gameserver.communitybbs.BB.Post;
import org.l2j.gameserver.mobius.gameserver.communitybbs.BB.Topic;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TopicBBSManager extends BaseBBSManager
{
	private final List<Topic> _table = new CopyOnWriteArrayList<>();
	private final Map<Forum, Integer> _maxId = new ConcurrentHashMap<>();
	
	protected TopicBBSManager()
	{
		// Prevent external initialization.
	}
	
	public void addTopic(Topic tt)
	{
		_table.add(tt);
	}
	
	public void delTopic(Topic topic)
	{
		_table.remove(topic);
	}
	
	public void setMaxID(int id, Forum f)
	{
		_maxId.put(f, id);
	}
	
	public int getMaxID(Forum f)
	{
		final Integer i = _maxId.get(f);
		return i == null ? 0 : i;
	}
	
	public Topic getTopicByID(int idf)
	{
		for (Topic t : _table)
		{
			if (t.getID() == idf)
			{
				return t;
			}
		}
		return null;
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		if (ar1.equals("crea"))
		{
			final Forum f = ForumsBBSManager.getInstance().getForumByID(Integer.parseInt(ar2));
			if (f == null)
			{
				CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + ar2 + " is not implemented yet</center><br><br></body></html>", activeChar);
			}
			else
			{
				f.vload();
				final Topic t = new Topic(Topic.ConstructorType.CREATE, getInstance().getMaxID(f) + 1, Integer.parseInt(ar2), ar5, Calendar.getInstance().getTimeInMillis(), activeChar.getName(), activeChar.getObjectId(), Topic.MEMO, 0);
				f.addTopic(t);
				getInstance().setMaxID(t.getID(), f);
				final Post p = new Post(activeChar.getName(), activeChar.getObjectId(), Calendar.getInstance().getTimeInMillis(), t.getID(), f.getID(), ar4);
				PostBBSManager.getInstance().addPostByTopic(p, t);
				parsecmd("_bbsmemo", activeChar);
			}
		}
		else if (ar1.equals("del"))
		{
			final Forum f = ForumsBBSManager.getInstance().getForumByID(Integer.parseInt(ar2));
			if (f == null)
			{
				CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + ar2 + " does not exist !</center><br><br></body></html>", activeChar);
			}
			else
			{
				final Topic t = f.getTopic(Integer.parseInt(ar3));
				if (t == null)
				{
					CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the topic: " + ar3 + " does not exist !</center><br><br></body></html>", activeChar);
				}
				else
				{
					// CPost cp = null;
					final Post p = PostBBSManager.getInstance().getGPosttByTopic(t);
					if (p != null)
					{
						p.deleteme(t);
					}
					t.deleteme(f);
					parsecmd("_bbsmemo", activeChar);
				}
			}
		}
		else
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the command: " + ar1 + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbsmemo"))
		{
			showTopics(activeChar.getMemo(), activeChar, 1, activeChar.getMemo().getID());
		}
		else if (command.startsWith("_bbstopics;read"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			final int idf = Integer.parseInt(st.nextToken());
			final String index = st.hasMoreTokens() ? st.nextToken() : null;
			final int ind = index == null ? 1 : Integer.parseInt(index);
			showTopics(ForumsBBSManager.getInstance().getForumByID(idf), activeChar, ind, idf);
		}
		else if (command.startsWith("_bbstopics;crea"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			final int idf = Integer.parseInt(st.nextToken());
			showNewTopic(ForumsBBSManager.getInstance().getForumByID(idf), activeChar, idf);
		}
		else if (command.startsWith("_bbstopics;del"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			final int idf = Integer.parseInt(st.nextToken());
			final int idt = Integer.parseInt(st.nextToken());
			final Forum f = ForumsBBSManager.getInstance().getForumByID(idf);
			if (f == null)
			{
				CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + idf + " does not exist !</center><br><br></body></html>", activeChar);
			}
			else
			{
				final Topic t = f.getTopic(idt);
				if (t == null)
				{
					CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the topic: " + idt + " does not exist !</center><br><br></body></html>", activeChar);
				}
				else
				{
					// CPost cp = null;
					final Post p = PostBBSManager.getInstance().getGPosttByTopic(t);
					if (p != null)
					{
						p.deleteme(t);
					}
					t.deleteme(f);
					parsecmd("_bbsmemo", activeChar);
				}
			}
		}
		else
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	private void showNewTopic(Forum forum, L2PcInstance activeChar, int idf)
	{
		if (forum == null)
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + idf + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
		else if (forum.getType() == Forum.MEMO)
		{
			showMemoNewTopics(forum, activeChar);
		}
		else
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	private void showMemoNewTopics(Forum forum, L2PcInstance activeChar)
	{
		final String html = "<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">Memo Form</a></td></tr></table><img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0><tr><td width=610><img src=\"sek.cbui355\" width=\"610\" height=\"1\"><br1><img src=\"sek.cbui355\" width=\"610\" height=\"1\"></td></tr></table><table fixwidth=610 border=0 cellspacing=0 cellpadding=0><tr><td><img src=\"l2ui.mini_logo\" width=5 height=20></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=1></td><td align=center FIXWIDTH=60 height=29>&$413;</td><td FIXWIDTH=540><edit var = \"Title\" width=540 height=13></td><td><img src=\"l2ui.mini_logo\" width=5 height=1></td></tr></table><table fixwidth=610 border=0 cellspacing=0 cellpadding=0><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=1></td><td align=center FIXWIDTH=60 height=29 valign=top>&$427;</td><td align=center FIXWIDTH=540><MultiEdit var =\"Content\" width=535 height=313></td><td><img src=\"l2ui.mini_logo\" width=5 height=1></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr></table><table fixwidth=610 border=0 cellspacing=0 cellpadding=0><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=1></td><td align=center FIXWIDTH=60 height=29>&nbsp;</td><td align=center FIXWIDTH=70><button value=\"&$140;\" action=\"Write Topic crea " + forum.getID() + " Title Content Title\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td><td align=center FIXWIDTH=70><button value = \"&$141;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td><td align=center FIXWIDTH=400>&nbsp;</td><td><img src=\"l2ui.mini_logo\" width=5 height=1></td></tr></table></center></body></html>";
		send1001(html, activeChar);
		send1002(activeChar);
	}
	
	private void showTopics(Forum forum, L2PcInstance activeChar, int index, int idf)
	{
		if (forum == null)
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + idf + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
		else if (forum.getType() == Forum.MEMO)
		{
			showMemoTopics(forum, activeChar, index);
		}
		else
		{
			CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", activeChar);
		}
	}
	
	private void showMemoTopics(Forum forum, L2PcInstance activeChar, int index)
	{
		forum.vload();
		final StringBuilder html = new StringBuilder(2000);
		html.append("<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">Memo Form</a></td></tr></table><img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=2 bgcolor=888888 width=610><tr><td FIXWIDTH=5></td><td FIXWIDTH=415 align=center>&$413;</td><td FIXWIDTH=120 align=center></td><td FIXWIDTH=70 align=center>&$418;</td></tr></table>");
		final DateFormat dateFormat = DateFormat.getInstance();
		
		for (int i = 0, j = getMaxID(forum) + 1; i < (12 * index); j--)
		{
			if (j < 0)
			{
				break;
			}
			final Topic t = forum.getTopic(j);
			if (t != null)
			{
				if (i++ >= (12 * (index - 1)))
				{
					html.append("<table border=0 cellspacing=0 cellpadding=5 WIDTH=610><tr><td FIXWIDTH=5></td><td FIXWIDTH=415><a action=\"bypass _bbsposts;read;" + forum.getID() + ";" + t.getID() + "\">" + t.getName() + "</a></td><td FIXWIDTH=120 align=center></td><td FIXWIDTH=70 align=center>" + dateFormat.format(new Date(t.getDate())) + "</td></tr></table><img src=\"L2UI.Squaregray\" width=\"610\" height=\"1\">");
				}
			}
		}
		
		html.append("<br><table width=610 cellspace=0 cellpadding=0><tr><td width=50><button value=\"&$422;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"></td><td width=510 align=center><table border=0><tr>");
		
		if (index == 1)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		else
		{
			html.append("<td><button action=\"bypass _bbstopics;read;" + forum.getID() + ";" + (index - 1) + "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		}
		
		int nbp = forum.getTopicSize() / 8;
		if ((nbp * 8) != ClanTable.getInstance().getClanCount())
		{
			nbp++;
		}
		for (int i = 1; i <= nbp; i++)
		{
			if (i == index)
			{
				html.append("<td> " + i + " </td>");
			}
			else
			{
				html.append("<td><a action=\"bypass _bbstopics;read;" + forum.getID() + ";" + i + "\"> " + i + " </a></td>");
			}
		}
		if (index == nbp)
		{
			html.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		else
		{
			html.append("<td><button action=\"bypass _bbstopics;read;" + forum.getID() + ";" + (index + 1) + "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		}
		
		html.append("</tr></table> </td> <td align=right><button value = \"&$421;\" action=\"bypass _bbstopics;crea;" + forum.getID() + "\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr><tr> <td></td><td align=center><table border=0><tr><td></td><td><edit var = \"Search\" width=130 height=11></td><td><button value=\"&$420;\" action=\"Write 5 -2 0 Search _ _\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td> </tr></table> </td></tr></table><br><br><br></center></body></html>");
		CommunityBoardHandler.separateAndSend(html.toString(), activeChar);
	}
	
	public static TopicBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final TopicBBSManager _instance = new TopicBBSManager();
	}
}