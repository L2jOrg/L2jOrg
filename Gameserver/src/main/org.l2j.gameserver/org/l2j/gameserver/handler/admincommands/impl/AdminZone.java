package org.l2j.gameserver.handler.admincommands.impl;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.templates.mapregion.DomainArea;
import org.l2j.gameserver.utils.ItemFunctions;

public class AdminZone implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_zone_check,
		admin_region,
		admin_pos,
		admin_vis_count,
		admin_domain,
		admin_loc,
		admin_locdump,
		admin_locmove,
		admin_loczone,
		admin_locspawn;
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(activeChar == null || !activeChar.getPlayerAccess().CanTeleport)
			return false;

		switch(command)
		{
			case admin_zone_check:
			{
				activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
				activeChar.sendMessage("Zone list:");
				List<Zone> zones = new ArrayList<Zone>();
				World.getZones(zones, activeChar.getLoc(), activeChar.getReflection());
				for(Zone zone : zones)
					activeChar.sendMessage(zone.getType().toString() + ", name: " + zone.getName() + ", state: " + (zone.isActive() ? "active" : "not active") + ", inside: " + zone.checkIfInZone(activeChar) + "/" + zone.checkIfInZone(activeChar.getX(), activeChar.getY(), activeChar.getZ()));

				break;
			}
			case admin_region:
			{
				activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
				activeChar.sendMessage("Objects list:");
				for(GameObject o : activeChar.getCurrentRegion())
					if(o != null)
						activeChar.sendMessage(o.toString());
				break;
			}
			case admin_vis_count:
			{
				activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
				activeChar.sendMessage("Players count: " + World.getAroundPlayers(activeChar).size());
				break;
			}
			case admin_pos:
			{
				String pos = activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ() + ", " + activeChar.getHeading() + " Geo [" + (activeChar.getX() - World.MAP_MIN_X >> 4) + ", " + (activeChar.getY() - World.MAP_MIN_Y >> 4) + "] Ref " + activeChar.getReflectionId();
				activeChar.sendMessage("Pos: " + pos);
				break;
			}
			case admin_domain:
			{
				DomainArea domain = MapRegionManager.getInstance().getRegionData(DomainArea.class, activeChar);
				Castle castle = domain != null ? ResidenceHolder.getInstance().getResidence(Castle.class, domain.getId()) : null;
				if(castle != null)
					activeChar.sendMessage("Domain: " + castle.getName());
				else
					activeChar.sendMessage("Domain: Unknown");
				break;
			}
			case admin_loc:
			{
				System.out.println("x=\"" + activeChar.getX() + "\" y=\"" + activeChar.getY() + "\" z=\"" + activeChar.getZ());
				activeChar.sendMessage("Point saved.");
				ItemInstance temp = ItemFunctions.createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				break;
			}
			case admin_locdump:
			{
				System.out.println("x=\"" + activeChar.getX() + "\" y=\"" + activeChar.getY() + "\" z=\"" + activeChar.getZ());
				activeChar.sendMessage("Point saved and dumped.");
				ItemInstance temp = ItemFunctions.createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				try
				{
					new File("dumps").mkdir();
					File f = new File("dumps/locdump.txt");
					if(!f.exists())
						f.createNewFile();
					FileWriter writer = new FileWriter(f, true);
					writer.write("Loc: " + activeChar.getLoc().x + ", " + activeChar.getLoc().y + ", " + activeChar.getLoc().z + "\n");
					writer.close();
				}
				catch (Exception e)
				{}
				break;
			}
			case admin_locmove:
			{
				if(wordList.length < 3)
				{
					activeChar.sendMessage("Use //locmove MIN_RANGE MAX_RANGE");
					return false;
				}

				int minRange = Integer.parseInt(wordList[1]);
				int maxRange = Integer.parseInt(wordList[2]);
				System.out.println("<move_to_point x=\"" + activeChar.getX() + "\" y=\"" + activeChar.getY() + "\" z=\"" + activeChar.getZ() + "\" min_range=\"" + minRange + "\" max_range=\"" + maxRange + "\"/>");
				activeChar.sendMessage("Move point saved.");
				ItemInstance temp = ItemFunctions.createItem(57);
				temp.dropMe(activeChar, activeChar.getLoc());
				break;
			}
			case admin_loczone:
			{
				System.out.println("<coords loc=\"" + activeChar.getX() + " " + activeChar.getY() + " " + (activeChar.getZ() - 150) + " " + (activeChar.getZ() + 150) + "\"/>");
				activeChar.sendMessage("Zone point saved.");
				ItemInstance temp = ItemFunctions.createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				break;
			}
			case admin_locspawn:
			{
				System.out.println("<add x=\"" + activeChar.getX() + "\" y=\"" + activeChar.getY() + "\" zmin=\"" + (activeChar.getZ() - 100) + "\" zmax=\"" + (activeChar.getZ() + 200) + "\"/>");
				activeChar.sendMessage("Zone point saved.");
				ItemInstance temp = ItemFunctions.createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				break;
			}
		}
		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}