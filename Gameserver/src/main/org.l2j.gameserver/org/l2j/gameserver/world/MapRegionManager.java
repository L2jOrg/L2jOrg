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
package org.l2j.gameserver.world;

import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Map Region Manager.
 *
 * @author Nyaran
 * @author joeAllisson
 */
public final class MapRegionManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapRegionManager.class);

    private final Map<String, MapRegion> regions = new HashMap<>();
    private final String defaultRespawn = "giran_castle_town";

    private MapRegionManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/mapregion/map-region.xsd");
    }

    @Override
    public void load() {
        regions.clear();
        parseDatapackDirectory("data/mapregion", false);
        LOGGER.info("Loaded {} map regions.", regions.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", nodeList -> forEach(nodeList, "region", regionNode -> {
           var attributes = regionNode.getAttributes();
           var name = parseString(attributes, "name");
           var town = parseString(attributes, "town");
           var loc = parseInteger(attributes, "loc");
           var bbs = parseInteger(attributes, "bbs");

           var region = new MapRegion(town, loc, bbs);
           parseRegion(regionNode, region);
           regions.put(name, region);
        }));
    }

    private void parseRegion(Node regionNode, MapRegion region) {
        for (Node node = regionNode.getFirstChild(); node != null; node = node.getNextSibling()) {
             var attributes = node.getAttributes();
             if ("respawn-point".equalsIgnoreCase(node.getNodeName())) {
                 final int spawnX = parseInteger(attributes, "x");
                 final int spawnY = parseInteger(attributes, "y");
                 final int spawnZ = parseInteger(attributes, "z");

                 if (parseBoolean(attributes, "chaotic")) {
                     region.addChaoticSpawn(spawnX, spawnY, spawnZ);
                 } else {
                     region.addSpawn(spawnX, spawnY, spawnZ);
                 }

             } else if ("map".equalsIgnoreCase(node.getNodeName())) {
                 region.addMapTile(parseByte(attributes, "x"), parseByte(attributes, "y"));
             } else if ("banned".equalsIgnoreCase(node.getNodeName())) {
                 region.addBannedRace(attributes.getNamedItem("race").getNodeValue(), attributes.getNamedItem("point").getNodeValue());
             }
         }
    }

    public final int getMapRegionLocId(WorldObject obj) {
        return isNull(obj) ? 0 : getMapRegionLocId(obj.getX(), obj.getY());
    }

    public final int getMapRegionLocId(int locX, int locY) {
        return zeroIfNullOrElse(getMapRegion(locX, locY), MapRegion::getLocId);
    }

    private MapRegion getMapRegion(WorldObject obj) {
        return getMapRegion(obj.getX(), obj.getY());
    }

    private MapRegion getMapRegion(int locX, int locY) {
        var regionX = getMapRegionX(locX);
        var regionY = getMapRegionY(locY);
        return regions.values().stream().filter(r -> r.isZoneInRegion(regionX, regionY)).findAny().orElse(null);
    }

    public final int getMapRegionX(int posX) {
        return (posX >> 15) + World.TILE_ZERO_COORD_X;
    }

    public final int getMapRegionY(int posY) {
        return (posY >> 15) + World.TILE_ZERO_COORD_Y;
    }

    public String getClosestTownName(Creature activeChar) {
        final MapRegion region = getMapRegion(activeChar);
        return isNull(region) ? "Aden Castle Town" : region.getTown();
    }

    public Location getTeleToLocation(Creature creature, TeleportWhereType teleportWhere) {
        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();

            if ((nonNull(player.getClan())) && !player.isFlyingMounted() && !player.isFlying()) // flying players in gracia cant use teleports to aden continent
            {
                var location = getResidenceLocation(teleportWhere, player);

                if (nonNull(location)) {
                    return location;
                }
            }

            // Karma player land out of city
            if (player.getReputation() < 0) {
                return getChaoticLocation(player);
            }

            // Checking if needed to be respawned in "far" town from the castle;
            Castle castle = CastleManager.getInstance().getCastle(player);
            if (nonNull(castle) && castle.getSiege().isInProgress()) {
                // Check if player's clan is participating
                if ( castle.getSiege().checkIsDefender(player.getClan()) || castle.getSiege().checkIsAttacker(player.getClan()) ) {
                    return castle.getResidenceZone().getOtherSpawnLoc();
                }
            }

            // Checking if in an instance
            final Instance inst = player.getInstanceWorld();
            if (inst != null) {
                final Location loc = inst.getExitLocation(player);
                if (loc != null) {
                    return loc;
                }
            }

            final RespawnZone zone = ZoneManager.getInstance().getZone(player, RespawnZone.class);
            if (nonNull(zone)) {
                return getRestartRegion(player, zone.getRespawnPoint(player)).getSpawnLoc();
            }
        }

        try {
            return getMapRegion(creature).getSpawnLoc();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            // Port to the default respawn if no closest town found.
            return regions.get(defaultRespawn).getSpawnLoc();
        }
    }

    private Location getChaoticLocation(Player player) {
        try {
            final RespawnZone zone = ZoneManager.getInstance().getZone(player, RespawnZone.class);
            if(nonNull(zone)) {
                return getRestartRegion(player, zone.getRespawnPoint(player)).getChaoticSpawnLoc();
            }

            if (getMapRegion(player).getBannedRaces().containsKey(player.getRace()))
            {
                return regions.get(getMapRegion(player).getBannedRaces().get(player.getRace())).getChaoticSpawnLoc();
            }

            return  getMapRegion(player).getChaoticSpawnLoc();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            if (player.isFlyingMounted()) {
                return regions.get("union_base_of_kserth").getChaoticSpawnLoc();
            }
            return regions.get(defaultRespawn).getChaoticSpawnLoc();
        }
    }

    private Location getResidenceLocation(TeleportWhereType teleportWhere, Player player) {
        Location location = null;
        if (teleportWhere == TeleportWhereType.CLANHALL) {
            location = getClanHallLocation(player);
        } else if (teleportWhere == TeleportWhereType.CASTLE) {
            location = getCastleLocation(player);
        }  else if (teleportWhere == TeleportWhereType.SIEGEFLAG) {
            location = getSiegeFlagLocation(player);
        }
        return location;
    }

    private Location getClanHallLocation(Player player) {
        if(player.isFlyingMounted()) {
            return null;
        }

        var clanHall = ClanHallManager.getInstance().getClanHallByClan(player.getClan());
        if ((nonNull(clanHall))) {
            return clanHall.getOwnerLocation();
        }
        return null;
    }

    private Location getCastleLocation(Player player) {
        var clan = player.getClan();
        var castle = CastleManager.getInstance().getCastleByOwner(clan);
        // Otherwise check if player is on castle or fortress ground
        // and player's clan is defender
        if (isNull(castle)) {
            castle = CastleManager.getInstance().getCastle(player);
            Siege siege;
            if (! (nonNull(castle) && (siege = castle.getSiege()).isInProgress() && nonNull(siege.getDefenderClan(clan))) ) {
                return null;
            }
        }

        if (castle.getId() > 0) {
            if (player.getReputation() < 0) {
                return castle.getResidenceZone().getChaoticSpawnLoc();
            }
            return castle.getResidenceZone().getSpawnLoc();
        }
        return null;
    }
    
    private Location getSiegeFlagLocation(Player player) {
        Castle castle = CastleManager.getInstance().getCastle(player);
        Set<Npc> flags = null;
        if (nonNull(castle) && castle.getSiege().isInProgress()) {
            flags = castle.getSiege().getFlag(player.getClan());
        }

        if(isNull(flags) || flags.isEmpty()) {
            return null;
        }

        return flags.stream().min(Comparator.comparingDouble(flag -> MathUtil.calculateDistance3D(flag, player))).map(WorldObject::getLocation).orElse(null);
    }

    public MapRegion getRestartRegion(Player player, String point) {
        try {
            final MapRegion region = regions.get(point);

            if (region.getBannedRaces().containsKey(player.getRace())) {
                getRestartRegion(player, region.getBannedRaces().get(player.getRace()));
            }
            return region;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return regions.get(defaultRespawn);
        }
    }

    /**
     * @param regionName the map region name.
     * @return if exists the map region identified by that name, null otherwise.
     */
    public MapRegion getMapRegionByName(String regionName) {
        return regions.get(regionName);
    }

    public int getBBs(ILocational loc) {
        final MapRegion region = getMapRegion(loc.getX(), loc.getY());
        return nonNull(region) ? region.getBbs() : regions.get(defaultRespawn).getBbs();
    }

    public static void init() {
        getInstance().load();
    }

    public static MapRegionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final MapRegionManager INSTANCE = new MapRegionManager();
    }
}
