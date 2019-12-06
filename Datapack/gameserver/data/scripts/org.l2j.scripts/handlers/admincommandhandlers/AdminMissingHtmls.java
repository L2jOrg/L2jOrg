package handlers.admincommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.*;
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
