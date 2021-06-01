/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;

import java.util.StringTokenizer;

import static java.util.Objects.isNull;

/**
 * Small typo fix by Zoey76 24/02/2011
 */
public class AdminZone implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS = {
        "admin_zone_check",
        "admin_zone_visual",
    };

    @Override
    public boolean useAdminCommand(String command, Player player)
    {
        if (isNull(player)) {
            return false;
        }

        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken(); // Get actual command

        if (actualCommand.equalsIgnoreCase("admin_zone_check"))
        {
            showHtml(player);
            BuilderUtil.sendSysMessage(player, "MapRegion: x:" + MapRegionManager.getInstance().getMapRegionX(player.getX()) + " y:" + MapRegionManager.getInstance().getMapRegionY(player.getY()) + " (" + MapRegionManager.getInstance().getMapRegionLocId(player) + ")");
            getGeoRegionXY(player);
            BuilderUtil.sendSysMessage(player, "Closest Town: " + MapRegionManager.getInstance().getClosestTownName(player));

            // Prevent exit instance variable deletion.
            if (!player.isInInstance())
            {
                Location loc;

                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
                BuilderUtil.sendSysMessage(player, "TeleToLocation (Castle): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CLANHALL);
                BuilderUtil.sendSysMessage(player, "TeleToLocation (ClanHall): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.SIEGEFLAG);
                BuilderUtil.sendSysMessage(player, "TeleToLocation (SiegeFlag): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());

                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
                BuilderUtil.sendSysMessage(player, "TeleToLocation (Town): x:" + loc.getX() + " y:" + loc.getY() + " z:" + loc.getZ());
            }
        }
        else if (actualCommand.equalsIgnoreCase("admin_zone_visual"))
        {
            if(!st.hasMoreTokens()) {
                showHtml(player);
                return true;
            }

            final String next = st.nextToken();
            if (next.equalsIgnoreCase("all")) {
                ZoneEngine.getInstance().forEachZone(player, z -> z.visualizeZone(player));
                for (SpawnTerritory territory : ZoneEngine.getInstance().getSpawnTerritories(player)) {
                    territory.visualizeZone(player);
                }
                showHtml(player);
            }
            else
            {
                final int zoneId = Integer.parseInt(next);
                ZoneEngine.getInstance().getZoneById(zoneId).visualizeZone(player);
            }
        }
        return true;
    }

    private static void showHtml(Player player)
    {
        final String htmContent = HtmCache.getInstance().getHtm(player, "data/html/admin/zone.htm");
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setHtml(htmContent);
        adminReply.replace("%PEACE%", player.isInsideZone(ZoneType.PEACE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%PVP%", player.isInsideZone(ZoneType.PVP) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%SIEGE%", player.isInsideZone(ZoneType.SIEGE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%CASTLE%", player.isInsideZone(ZoneType.CASTLE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%FORT%", player.isInsideZone(ZoneType.FORT) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%HQ%", player.isInsideZone(ZoneType.HQ) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%CLANHALL%", player.isInsideZone(ZoneType.CLAN_HALL) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%LAND%", player.isInsideZone(ZoneType.LANDING) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%NOLAND%", player.isInsideZone(ZoneType.NO_LANDING) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%NOSUMMON%", player.isInsideZone(ZoneType.NO_SUMMON_FRIEND) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%WATER%", player.isInsideZone(ZoneType.WATER) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%FISHING%", player.isInsideZone(ZoneType.FISHING) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%SWAMP%", player.isInsideZone(ZoneType.SWAMP) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%DANGER%", player.isInsideZone(ZoneType.DANGER_AREA) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%NOSTORE%", player.isInsideZone(ZoneType.NO_STORE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%SCRIPT%", player.isInsideZone(ZoneType.SCRIPT) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%TAX%", (player.isInsideZone(ZoneType.TAX) ? "<font color=\"LEVEL\">YES</font>" : "NO"));

        final StringBuilder zones = new StringBuilder(100);
        ZoneEngine.getInstance().forEachZone(player, zone -> appendZoneInfo(zones, zone));
        for (SpawnTerritory territory : ZoneEngine.getInstance().getSpawnTerritories(player))
        {
            zones.append(territory.getName());
            zones.append("<br1>");
        }
        adminReply.replace("%ZLIST%", zones.toString());
        player.sendPacket(adminReply);
    }

    private static void appendZoneInfo(StringBuilder zones, Zone zone) {
        if (zone.getName() != null) {
            zones.append(zone.getName());
            if (zone.getId() < 300000) {
                zones.append(" (");
                zones.append(zone.getId());
                zones.append(")");
            }
            zones.append("<br1>");
        } else {
            zones.append(zone.getId());
        }
        zones.append(" ");
    }

    private static void getGeoRegionXY(Player activeChar)
    {
        final int worldX = activeChar.getX();
        final int worldY = activeChar.getY();
        final int geoX = (((worldX - -327680) >> 4) >> 11) + 10;
        final int geoY = (((worldY - -262144) >> 4) >> 11) + 10;
        BuilderUtil.sendSysMessage(activeChar, "GeoRegion: " + geoX + "_" + geoY + "");
    }

    @Override
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}
