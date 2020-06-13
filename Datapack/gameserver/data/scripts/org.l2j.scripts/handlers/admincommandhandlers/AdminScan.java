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

import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.BypassBuilder;
import org.l2j.gameserver.util.BypassParser;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

import java.util.StringTokenizer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.MathUtil.calculateDistance2D;

/**
 * @author NosBit
 */
public class AdminScan implements IAdminCommandHandler
{
	private static final String SPACE = " ";
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_scan",
		"admin_deleteNpcByObjectId"
	};
	
	private static final int DEFAULT_RADIUS = 1000;
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_scan":
			{
				processBypass(activeChar, new BypassParser(command));
				break;
			}
			case "admin_deletenpcbyobjectid":
			{
				if (!st.hasMoreElements())
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //deletenpcbyobjectid objectId=<object_id>");
					return false;
				}
				
				final BypassParser parser = new BypassParser(command);
				try
				{
					final int objectId = parser.getInt("objectId", 0);
					if (objectId == 0)
					{
						BuilderUtil.sendSysMessage(activeChar, "objectId is not set!");
					}
					
					final WorldObject target = World.getInstance().findObject(objectId);
					final Npc npc = target instanceof Npc ? (Npc) target : null;
					if (npc == null)
					{
						BuilderUtil.sendSysMessage(activeChar, "NPC does not exist or object_id does not belong to an NPC");
						return false;
					}
					
					npc.deleteMe();
					
					final Spawn spawn = npc.getSpawn();
					if (spawn != null)
					{
						spawn.stopRespawn();
						
						if (DBSpawnManager.getInstance().isDefined(spawn.getId()))
						{
							DBSpawnManager.getInstance().deleteSpawn(spawn, true);
						}
						else
						{
							SpawnTable.getInstance().deleteSpawn(spawn, true);
						}
					}
					
					activeChar.sendMessage(npc.getName() + " have been deleted.");
				}
				catch (NumberFormatException e)
				{
					BuilderUtil.sendSysMessage(activeChar, "object_id must be a number.");
					return false;
				}
				
				processBypass(activeChar, parser);
				break;
			}
		}
		return true;
	}
	
	private void processBypass(Player activeChar, BypassParser parser)
	{
		final int id = parser.getInt("id", 0);
		final String name = parser.getString("name", null);
		final int radius = parser.getInt("radius", parser.getInt("range", DEFAULT_RADIUS));
		final int page = parser.getInt("page", 0);
		
		final Predicate<Npc> condition;
		if (id > 0)
		{
			condition = npc -> npc.getId() == id;
		}
		else if (name != null)
		{
			condition = npc -> npc.getName().toLowerCase().startsWith(name.toLowerCase());
		}
		else
		{
			condition = npc -> true;
		}
		
		sendNpcList(activeChar, radius, page, condition, parser);
	}
	
	private BypassBuilder createBypassBuilder(BypassParser parser, String bypass)
	{
		final int id = parser.getInt("id", 0);
		final String name = parser.getString("name", null);
		final int radius = parser.getInt("radius", parser.getInt("range", DEFAULT_RADIUS));
		final BypassBuilder builder = new BypassBuilder(bypass);
		
		if (id > 0)
		{
			builder.addParam("id", id);
		}
		else if (name != null)
		{
			builder.addParam("name", name);
		}
		
		if (radius > DEFAULT_RADIUS)
		{
			builder.addParam("radius", radius);
		}
		return builder;
	}
	
	private void sendNpcList(Player activeChar, int radius, int page, Predicate<Npc> condition, BypassParser parser)
	{
		final BypassBuilder bypassParser = createBypassBuilder(parser, "bypass -h admin_scan");
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/scan.htm");
		
		//@formatter:off
		final PageResult result = PageBuilder.newBuilder(World.getInstance().getVisibleObjectsInRange(activeChar, Npc.class, radius, condition), 15, bypassParser.toString())
			.currentPage(page)
			.pageHandler(NextPrevPageHandler.INSTANCE)
			.formatter(BypassParserFormatter.INSTANCE)
			.style(ButtonsStyle.INSTANCE)
			.bodyHandler((pages, character, sb) ->
		{
			final BypassBuilder builder = createBypassBuilder(parser, "bypass -h admin_deleteNpcByObjectId");
			final String npcName = character.getName();
			builder.addParam("page", page);
			builder.addParam("objectId", character.getObjectId());
			
			sb.append("<tr>");
			sb.append("<td width=\"45\">").append(character.getId()).append("</td>");
			sb.append("<td><a action=\"bypass -h admin_move_to ").append(character.getX()).append(SPACE).append(character.getY()).append(SPACE).append(character.getZ()).append("\">").append(npcName.isEmpty() ? "No name NPC" : npcName).append("</a></td>");
			sb.append("<td width=\"60\">").append(GameUtils.formatAdena(Math.round(calculateDistance2D(activeChar, character)))).append("</td>");
			sb.append("<td width=\"54\"><a action=\"").append(builder.toStringBuilder()).append("\"><font color=\"LEVEL\">Delete</font></a></td>");
			sb.append("</tr>");
		}).build();
		//@formatter:on
		
		if (result.getPages() > 0)
		{
			html.replace("%pages%", "<center><table width=\"100%\" cellspacing=0><tr>" + result.getPagerTemplate() + "</tr></table></center>");
		}
		else
		{
			html.replace("%pages%", "");
		}
		
		html.replace("%data%", result.getBodyTemplate().toString());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
