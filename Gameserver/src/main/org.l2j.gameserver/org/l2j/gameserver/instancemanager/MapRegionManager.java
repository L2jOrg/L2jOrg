package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.MapRegion;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.zone.type.RespawnZone;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Map Region Manager.
 *
 * @author Nyaran
 */
public final class MapRegionManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapRegionManager.class);

    private final Map<String, MapRegion> regions = new HashMap<>();
    private final String defaultRespawn = "talking_island_town";

    private MapRegionManager() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/mapregion/MapRegion.xsd");
    }

    @Override
    public void load() {
        regions.clear();
        parseDatapackDirectory("data/mapregion", false);
        LOGGER.info("Loaded {}  map regions.", regions.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        String name;
        String town;
        int locId;
        int bbs;

        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("region".equalsIgnoreCase(d.getNodeName())) {
                        attrs = d.getAttributes();
                        name = attrs.getNamedItem("name").getNodeValue();
                        town = attrs.getNamedItem("town").getNodeValue();
                        locId = parseInteger(attrs, "locId");
                        bbs = parseInteger(attrs, "bbs");

                        final MapRegion region = new MapRegion(name, town, locId, bbs);
                        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                            attrs = c.getAttributes();
                            if ("respawnPoint".equalsIgnoreCase(c.getNodeName())) {
                                final int spawnX = parseInteger(attrs, "x");
                                final int spawnY = parseInteger(attrs, "y");
                                final int spawnZ = parseInteger(attrs, "z");

                                final boolean chaotic = parseBoolean(attrs, "isChaotic", false);

                                if (chaotic) {
                                    region.addChaoticSpawn(spawnX, spawnY, spawnZ);
                                } else {
                                    region.addSpawn(spawnX, spawnY, spawnZ);
                                }
                            } else if ("map".equalsIgnoreCase(c.getNodeName())) {
                                region.addMap(parseInteger(attrs, "X"), parseInteger(attrs, "Y"));
                            } else if ("banned".equalsIgnoreCase(c.getNodeName())) {
                                region.addBannedRace(attrs.getNamedItem("race").getNodeValue(), attrs.getNamedItem("point").getNodeValue());
                            }
                        }
                        regions.put(name, region);
                    }
                }
            }
        }
    }

    public final MapRegion getMapRegion(int locX, int locY) {
        return regions.values().stream().filter(r -> r.isZoneInRegion(getMapRegionX(locX), getMapRegionY(locY))).findAny().orElse(null);
    }


    public final int getMapRegionLocId(int locX, int locY) {
        final MapRegion region = getMapRegion(locX, locY);
        if (nonNull(region)) {
            return region.getLocId();
        }
        return 0;
    }

    public final MapRegion getMapRegion(WorldObject obj) {
        return getMapRegion(obj.getX(), obj.getY());
    }

    public final int getMapRegionLocId(WorldObject obj) {
        return getMapRegionLocId(obj.getX(), obj.getY());
    }

    public final int getMapRegionX(int posX) {
        return (posX >> 15) + 9 + 11;
    }

    public final int getMapRegionY(int posY) {
        return (posY >> 15) + 10 + 8;
    }

    public String getClosestTownName(Creature activeChar) {
        final MapRegion region = getMapRegion(activeChar);
        return isNull(region) ? "Aden Castle Town" : region.getTown();
    }

    public Location getTeleToLocation(Creature activeChar, TeleportWhereType teleportWhere) {
        if (isPlayer(activeChar)) {
            final Player player = activeChar.getActingPlayer();

            Castle castle;
            Fort fort;

            Location location = null;

            if ((nonNull(player.getClan())) && !player.isFlyingMounted() && !player.isFlying()) // flying players in gracia cant use teleports to aden continent
            {
                if (teleportWhere == TeleportWhereType.CLANHALL) {
                    location = getClanHallLocation(player);
                }
                else if (teleportWhere == TeleportWhereType.CASTLE) {
                    location = getCastleLocation(player);
                }
                else if (teleportWhere == TeleportWhereType.FORTRESS) {
                    location = getFortLocation(player);
                }
                else if (teleportWhere == TeleportWhereType.SIEGEFLAG) {
                    location = getSiegeFlagLocation(player);
                }

                if(nonNull(location)) {
                    return location;
                }
            }


            // Karma player land out of city
            if (player.getReputation() < 0) {
                try {
                    final RespawnZone zone = ZoneManager.getInstance().getZone(player, RespawnZone.class);
                    if (zone != null) {
                        return getRestartRegion(activeChar, zone.getRespawnPoint((Player) activeChar)).getChaoticSpawnLoc();
                    }
                    return getMapRegion(activeChar).getChaoticSpawnLoc();
                } catch (Exception e) {
                    if (player.isFlyingMounted()) {
                        return regions.get("union_base_of_kserth").getChaoticSpawnLoc();
                    }
                    return regions.get(defaultRespawn).getChaoticSpawnLoc();
                }
            }

            // Checking if needed to be respawned in "far" town from the castle;
            castle = CastleManager.getInstance().getCastle(player);
            if (castle != null) {
                if (castle.getSiege().isInProgress()) {
                    // Check if player's clan is participating
                    if ((castle.getSiege().checkIsDefender(player.getClan()) || castle.getSiege().checkIsAttacker(player.getClan()))) {
                        return castle.getResidenceZone().getOtherSpawnLoc();
                    }
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
        }

        // Get the nearest town
        try {
            final RespawnZone zone = ZoneManager.getInstance().getZone(activeChar, RespawnZone.class);
            if (zone != null) {
                return getRestartRegion(activeChar, zone.getRespawnPoint((Player) activeChar)).getSpawnLoc();
            }
            return getMapRegion(activeChar).getSpawnLoc();
        } catch (Exception e) {
            // Port to the default respawn if no closest town found.
            return regions.get(defaultRespawn).getSpawnLoc();
        }
    }

    private Location getClanHallLocation(Player player) {
        if(player.isFlyingMounted()) {
            return null;
        }

        var clanhall = ClanHallData.getInstance().getClanHallByClan(player.getClan());
        if ((nonNull(clanhall))) {
            return clanhall.getOwnerLocation();
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

        if (castle.getResidenceId() > 0) {
            if (player.getReputation() < 0) {
                return castle.getResidenceZone().getChaoticSpawnLoc();
            }
            return castle.getResidenceZone().getSpawnLoc();
        }
        return null;
    }

    private Location getFortLocation(Player player) {
        Fort fort = FortDataManager.getInstance().getFortByOwner(player.getClan());

        if (isNull(fort)) {
            fort = FortDataManager.getInstance().getFort(player);
            if (!(( nonNull(fort)) && fort.getSiege().isInProgress() && (fort.getOwnerClan() == player.getClan()))) {
               return null;
            }
        }

        if (fort.getResidenceId() > 0) {
            if (player.getReputation() < 0) {
                return fort.getResidenceZone().getChaoticSpawnLoc();
            }
            return fort.getResidenceZone().getSpawnLoc();
        }
        return null;
    }
    
    private Location getSiegeFlagLocation(Player player) {
        Castle castle = CastleManager.getInstance().getCastle(player);
        Fort fort;
        Set<Npc> flags = null;
        if (nonNull(castle) && castle.getSiege().isInProgress()) {
            flags = castle.getSiege().getFlag(player.getClan());
        } else if (nonNull(fort = FortDataManager.getInstance().getFort(player)) && fort.getSiege().isInProgress()) {
            flags = fort.getSiege().getFlag(player.getClan());
        }

        if(isNull(flags) || flags.isEmpty()) {
            return null;
        }

        return flags.stream().min(Comparator.comparingDouble(flag -> MathUtil.calculateDistance3D(flag, player))).map(WorldObject::getLocation).orElse(null);
    }

    public MapRegion getRestartRegion(Creature activeChar, String point) {
        try {
            final Player player = (Player) activeChar;
            final MapRegion region = regions.get(point);

            if (region.getBannedRace().containsKey(player.getRace())) {
                getRestartRegion(player, region.getBannedRace().get(player.getRace()));
            }
            return region;
        } catch (Exception e) {
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
        return region != null ? region.getBbs() : regions.get(defaultRespawn).getBbs();
    }


    public static MapRegionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final MapRegionManager INSTANCE = new MapRegionManager();
    }
}
