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
package handlers.admincommandhandlers;

import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.instance.Observation;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author Mobius
 */
public class AdminMissingHtmls implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_geomap_missing_htmls",
		"admin_world_missing_htmls"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_geomap_missing_htmls":
			{
				final int x = ((activeChar.getX() - World.MAP_MIN_X) >> 15) + World.TILE_X_MIN;
				final int y = ((activeChar.getY() - World.MAP_MIN_Y) >> 15) + World.TILE_Y_MIN;
				final int topLeftX = (x - World.TILE_ZERO_COORD_X) * World.TILE_SIZE;
				final int topLeftY = (y - World.TILE_ZERO_COORD_Y) * World.TILE_SIZE;
				final int bottomRightX = (((x - World.TILE_ZERO_COORD_X) * World.TILE_SIZE) + World.TILE_SIZE) - 1;
				final int bottomRightY = (((y - World.TILE_ZERO_COORD_Y) * World.TILE_SIZE) + World.TILE_SIZE) - 1;
				BuilderUtil.sendSysMessage(activeChar, "GeoMap: " + x + "_" + y + " (" + topLeftX + "," + topLeftY + " to " + bottomRightX + "," + bottomRightY + ")");
				final List<Integer> results = new ArrayList<>();
				for (WorldObject obj : World.getInstance().getVisibleObjects())
				{
					if (isNpc(obj) //
						&& !isMonster(obj) //
						&& !(obj instanceof Observation) //
						&& !(obj instanceof Artefact) //
						&& !results.contains(obj.getId()))
					{
						final Npc npc = (Npc) obj;
						if ((npc.getLocation().getX() > topLeftX) && (npc.getLocation().getX() < bottomRightX) && (npc.getLocation().getY() > topLeftY) && (npc.getLocation().getY() < bottomRightY) && npc.isTalkable() && !npc.hasListener(EventType.ON_NPC_FIRST_TALK) && (npc.getHtmlPath(npc.getId(), 0) == "data/html/npcdefault.htm"))
						{
							results.add(npc.getId());
						}
					}
				}
				Collections.sort(results);
				for (int id : results)
				{
					BuilderUtil.sendSysMessage(activeChar, "NPC " + id + " does not have a default html.");
				}
				BuilderUtil.sendSysMessage(activeChar, "Found " + results.size() + " results.");
				break;
			}
			case "admin_world_missing_htmls":
			{
				BuilderUtil.sendSysMessage(activeChar, "Missing htmls for the whole world.");
				final List<Integer> results = new ArrayList<>();
				for (WorldObject obj : World.getInstance().getVisibleObjects())
				{
					if (isNpc(obj) //
						&& !isMonster(obj) //
						&& !(obj instanceof Observation) //
						&& !(obj instanceof Artefact) //
						&& !results.contains(obj.getId()))
					{
						final Npc npc = (Npc) obj;
						if (npc.isTalkable() && !npc.hasListener(EventType.ON_NPC_FIRST_TALK) && (npc.getHtmlPath(npc.getId(), 0) == "data/html/npcdefault.htm"))
						{
							results.add(npc.getId());
						}
					}
				}
				Collections.sort(results);
				for (int id : results)
				{
					BuilderUtil.sendSysMessage(activeChar, "NPC " + id + " does not have a default html.");
				}
				BuilderUtil.sendSysMessage(activeChar, "Found " + results.size() + " results.");
				break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
