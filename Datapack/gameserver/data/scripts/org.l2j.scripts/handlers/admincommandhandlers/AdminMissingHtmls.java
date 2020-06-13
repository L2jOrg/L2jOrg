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
package handlers.admincommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.*;

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
						&& !isArtifact(obj)

						&& !(obj instanceof Observation) //
						&& !results.contains(obj.getId()))
					{
						final Npc npc = (Npc) obj;
						if ((npc.getLocation().getX() > topLeftX) && (npc.getLocation().getX() < bottomRightX) && (npc.getLocation().getY() > topLeftY) && (npc.getLocation().getY() < bottomRightY) && npc.isTalkable() && !npc.hasListener(EventType.ON_NPC_FIRST_TALK))
						{
							if ((npc.getHtmlPath(npc.getId(), 0).equals("data/html/npcdefault.htm")) //
									|| ((obj instanceof Fisherman) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/fisherman/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Warehouse) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/warehouse/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Merchant && !(obj instanceof Fisherman)) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/merchant/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Guard) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/guard/" + npc.getId() + ".htm"))))
							{
								results.add(npc.getId());
							}
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
						&& !isArtifact(obj)
						&& !(obj instanceof Observation) //
						&& !results.contains(obj.getId()))
					{
						final Npc npc = (Npc) obj;
						if (npc.isTalkable() && !npc.hasListener(EventType.ON_NPC_FIRST_TALK))
						{
							if ((npc.getHtmlPath(npc.getId(), 0).equals("data/html/npcdefault.htm") //
									|| ((obj instanceof Fisherman) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/fisherman/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Warehouse) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/warehouse/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Merchant &&  !(obj instanceof Fisherman)) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/merchant/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Guard) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/guard/" + npc.getId() + ".htm")))))
							{
								results.add(npc.getId());
							}
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
			case "admin_next_missing_html":
			{
				for (WorldObject obj : World.getInstance().getVisibleObjects())
				{
					if (isNpc(obj) //
							&& !isMonster(obj) //
							&& !isArtifact(obj)
							&& !(obj instanceof Observation) //
							&& !(obj instanceof FlyTerrainObject))
					{
						final Npc npc = (Npc) obj;
						if (npc.isTalkable() && !npc.hasListener(EventType.ON_NPC_FIRST_TALK))
						{
							if ((npc.getHtmlPath(npc.getId(), 0).equals("data/html/npcdefault.htm")) //
									|| ((obj instanceof Fisherman) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/fisherman/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Warehouse) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/warehouse/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Merchant && !(obj instanceof Fisherman)) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/merchant/" + npc.getId() + ".htm"))) //
									|| ((obj instanceof Guard) && isNullOrEmpty(HtmCache.getInstance().getHtm(null, "data/html/guard/" + npc.getId() + ".htm"))))
							{
								activeChar.teleToLocation(npc);
								BuilderUtil.sendSysMessage(activeChar, "NPC " + npc.getId() + " does not have a default html.");
								break;
							}
						}
					}
				}
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
