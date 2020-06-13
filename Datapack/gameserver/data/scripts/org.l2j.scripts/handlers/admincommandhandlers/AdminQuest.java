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

import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestTimer;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import static org.l2j.commons.util.Util.isDigit;
import static org.l2j.gameserver.util.GameUtils.isCreature;


public class AdminQuest implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminQuest.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_quest_reload",
		"admin_script_load",
		"admin_script_unload",
		"admin_script_dir",
		"admin_show_quests",
		"admin_quest_info"
	};
	
	private static Quest findScript(String script)
	{
		if (isDigit(script))
		{
			return QuestManager.getInstance().getQuest(Integer.parseInt(script));
		}
		return QuestManager.getInstance().getQuest(script);
	}
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_quest_reload"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token
			
			if (!st.hasMoreTokens())
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //quest_reload <questName> or <questId>");
				return false;
			}
			
			final String script = st.nextToken();
			final Quest quest = findScript(script);
			if (quest == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "The script " + script + " couldn't be found!");
				return false;
			}
			
			if (!quest.reload())
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed to reload " + script + "!");
				return false;
			}
			
			BuilderUtil.sendSysMessage(activeChar, "Script successful reloaded.");
		}
		else if (command.startsWith("admin_script_load"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token
			
			if (!st.hasMoreTokens())
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //script_load path/to/script.java");
				return false;
			}
			
			final String script = st.nextToken();
			try
			{
				ScriptEngineManager.getInstance().executeScript(Paths.get(script));
				BuilderUtil.sendSysMessage(activeChar, "Script loaded seccessful!");
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed to load script!");
				LOGGER.warn("Failed to load script " + script + "!", e);
			}
		}
		else if (command.startsWith("admin_script_unload"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // skip command token
			
			if (!st.hasMoreTokens())
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //script_load path/to/script.java");
				return false;
			}
			
			final String script = st.nextToken();
			final Quest quest = findScript(script);
			if (quest == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "The script " + script + " couldn't be found!");
				return false;
			}
			
			quest.unload();
			BuilderUtil.sendSysMessage(activeChar, "Script successful unloaded!");
		}
		else if (command.startsWith("admin_script_dir"))
		{
			final String[] parts = command.split(" ");
			if (parts.length == 1)
			{
				showDir(null, activeChar);
			}
			else
			{
				showDir(parts[1], activeChar);
			}
			
		}
		else if (command.startsWith("admin_show_quests"))
		{
			if (activeChar.getTarget() == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "Get a target first.");
			}
			else if (!isCreature(activeChar.getTarget()))
			{
				BuilderUtil.sendSysMessage(activeChar, "Invalid Target.");
			}
			else
			{
				final Creature character = (Creature) activeChar.getTarget();
				final StringBuilder sb = new StringBuilder();
				final Set<String> questNames = new TreeSet<>();
				for (EventType type : EventType.values())
				{
					for (AbstractEventListener listener : character.getListeners(type))
					{
						if (listener.getOwner() instanceof Quest)
						{
							final Quest quest = (Quest) listener.getOwner();
							if (!questNames.add(quest.getName()))
							{
								continue;
							}
							sb.append("<tr><td colspan=\"4\"><font color=\"LEVEL\"><a action=\"bypass -h admin_quest_info " + quest.getName() + "\">" + quest.getName() + "</a></font></td></tr>");
						}
					}
				}
				
				final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
				msg.setFile(activeChar, "data/html/admin/npc-quests.htm");
				msg.replace("%quests%", sb.toString());
				msg.replace("%objid%", character.getObjectId());
				msg.replace("%questName%", "");
				activeChar.sendPacket(msg);
			}
		}
		else if (command.startsWith("admin_quest_info "))
		{
			final String questName = command.substring("admin_quest_info ".length());
			final Quest quest = QuestManager.getInstance().getQuest(questName);
			String events = "";
			String npcs = "";
			String items = "";
			String timers = "";
			int counter = 0;
			if (quest == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "Couldn't find quest or script with name " + questName + " !");
				return false;
			}
			
			final Set<EventType> listenerTypes = new TreeSet<>();
			for (AbstractEventListener listener : quest.getListeners())
			{
				if (listenerTypes.add(listener.getType()))
				{
					events += ", " + listener.getType().name();
					counter++;
				}
				if (counter > 10)
				{
					counter = 0;
					break;
				}
			}
			
			final Set<Integer> npcIds = new TreeSet<>(quest.getRegisteredIds(ListenerRegisterType.NPC));
			for (int npcId : npcIds)
			{
				npcs += ", " + npcId;
				counter++;
				if (counter > 50)
				{
					counter = 0;
					break;
				}
			}
			
			if (!events.isEmpty())
			{
				events = listenerTypes.size() + ": " + events.substring(2);
			}
			
			if (!npcs.isEmpty())
			{
				npcs = npcIds.size() + ": " + npcs.substring(2);
			}
			
			if (quest.getRegisteredItemIds() != null)
			{
				for (int itemId : quest.getRegisteredItemIds())
				{
					items += ", " + itemId;
					counter++;
					if (counter > 20)
					{
						counter = 0;
						break;
					}
				}
				items = quest.getRegisteredItemIds().length + ":" + items.substring(2);
			}
			
			for (List<QuestTimer> list : quest.getQuestTimers().values())
			{
				for (QuestTimer timer : list)
				{
					timers += "<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">" + timer.getName() + ":</font> <font color=00FF00>Active: " + timer.getIsActive() + " Repeatable: " + timer.getIsRepeating() + " Player: " + timer.getPlayer() + " Npc: " + timer.getNpc() + "</font></td></tr></table></td></tr>";
					counter++;
					if (counter > 10)
					{
						break;
					}
				}
			}
			
			final StringBuilder sb = new StringBuilder();
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">ID:</font> <font color=00FF00>" + quest.getId() + "</font></td></tr></table></td></tr>");
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Name:</font> <font color=00FF00>" + quest.getName() + "</font></td></tr></table></td></tr>");
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Path:</font> <font color=00FF00>" + quest.getPath() + "</font></td></tr></table></td></tr>");
			sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Events:</font> <font color=00FF00>" + events + "</font></td></tr></table></td></tr>");
			if (!npcs.isEmpty())
			{
				sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">NPCs:</font> <font color=00FF00>" + npcs + "</font></td></tr></table></td></tr>");
			}
			if (!items.isEmpty())
			{
				sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Items:</font> <font color=00FF00>" + items + "</font></td></tr></table></td></tr>");
			}
			if (!timers.isEmpty())
			{
				sb.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Timers:</font> <font color=00FF00></font></td></tr></table></td></tr>");
				sb.append(timers);
			}
			
			final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
			msg.setFile(activeChar, "data/html/admin/npc-quests.htm");
			msg.replace("%quests%", sb.toString());
			msg.replace("%questName%", "<table><tr><td width=\"50\" align=\"left\"><a action=\"bypass -h admin_script_load " + quest.getName() + "\">Reload</a></td> <td width=\"150\"  align=\"center\"><a action=\"bypass -h admin_quest_info " + quest.getName() + "\">" + quest.getName() + "</a></td> <td width=\"50\" align=\"right\"><a action=\"bypass -h admin_script_unload " + quest.getName() + "\">Unload</a></td></tr></table>");
			activeChar.sendPacket(msg);
		}
		return true;
	}
	
	private void showDir(String dir, Player activeChar)
	{
		String replace = null;
		File path;
		String currentPath = "/";
		if ((dir == null) || dir.trim().isEmpty() || dir.contains(".."))
		{
			final StringBuilder sb = new StringBuilder(200);
			path = ScriptEngineManager.SCRIPT_FOLDER.toFile();
			final String[] children = path.list();
			Arrays.sort(children);
			for (String c : children)
			{
				final File n = new File(path, c);
				if (n.isHidden() || n.getName().startsWith("."))
				{
					continue;
				}
				else if (n.isDirectory())
				{
					sb.append("<a action=\"bypass -h admin_script_dir " + c + "\">" + c + "</a><br1>");
				}
				else if (c.endsWith(".java") || c.endsWith(".py"))
				{
					sb.append("<a action=\"bypass -h admin_script_load " + c + "\"><font color=\"LEVEL\">" + c + "</font></a><br1>");
				}
			}
			replace = sb.toString();
		}
		else
		{
			path = new File(ScriptEngineManager.SCRIPT_FOLDER.toFile(), dir);
			if (!path.isDirectory())
			{
				BuilderUtil.sendSysMessage(activeChar, "Wrong path.");
				return;
			}
			currentPath = dir;
			final boolean questReducedNames = currentPath.equalsIgnoreCase("quests");
			final StringBuilder sb = new StringBuilder(200);
			sb.append("<a action=\"bypass -h admin_script_dir " + getUpPath(currentPath) + "\">..</a><br1>");
			final String[] children = path.list();
			Arrays.sort(children);
			for (String c : children)
			{
				final File n = new File(path, c);
				if (n.isHidden() || n.getName().startsWith("."))
				{
					continue;
				}
				else if (n.isDirectory())
				{
					sb.append("<a action=\"bypass -h admin_script_dir " + currentPath + "/" + c + "\">" + (questReducedNames ? getQuestName(c) : c) + "</a><br1>");
				}
				else if (c.endsWith(".java") || c.endsWith(".py"))
				{
					sb.append("<a action=\"bypass -h admin_script_load " + currentPath + "/" + c + "\"><font color=\"LEVEL\">" + c + "</font></a><br1>");
				}
			}
			replace = sb.toString();
			if (questReducedNames)
			{
				currentPath += " (limited list - HTML too long)";
			}
		}
		
		if (replace.length() > 17200)
		{
			replace = replace.substring(0, 17200); // packetlimit
		}
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/scriptdirectory.htm");
		html.replace("%path%", currentPath);
		html.replace("%list%", replace);
		activeChar.sendPacket(html);
	}
	
	private String getUpPath(String full)
	{
		final int index = full.lastIndexOf("/");
		if (index == -1)
		{
			return "";
		}
		return full.substring(0, index);
	}
	
	private String getQuestName(String full)
	{
		return full.split("_")[0];
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}