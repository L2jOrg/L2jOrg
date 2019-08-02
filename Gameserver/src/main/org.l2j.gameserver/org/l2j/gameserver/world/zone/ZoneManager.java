package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.form.ZoneCuboid;
import org.l2j.gameserver.world.zone.form.ZoneCylinder;
import org.l2j.gameserver.world.zone.form.ZoneNPoly;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * This class manages the zones
 *
 * @author durgus
 */
public final class ZoneManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneManager.class);

    private static final Map<String, AbstractZoneSettings> SETTINGS = new HashMap<>();

    private static final int SHIFT_BY = 15;
    private static final int OFFSET_X = Math.abs(World.MAP_MIN_X >> SHIFT_BY);
    private static final int OFFSET_Y = Math.abs(World.MAP_MIN_Y >> SHIFT_BY);

    private final Map<Class<? extends Zone>, IntMap<? extends Zone>> _classZones = new HashMap<>();
    private final Map<String, SpawnTerritory> _spawnTerritories = new HashMap<>();
    private final ZoneRegion[][] zoneRegions;
    private int _lastDynamicId = 300000;
    private List<Item> _debugItems;

    private ZoneManager() {
        zoneRegions = new ZoneRegion[(World.MAP_MAX_X >> SHIFT_BY) + OFFSET_X + 1][(World.MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y + 1];
        for (int x = 0; x < zoneRegions.length; x++) {
            for (int y = 0; y < zoneRegions[x].length; y++) {
                zoneRegions[x][y] = new ZoneRegion(x, y);
            }
        }
        LOGGER.info("Zone Region Grid set up: {} by {}", zoneRegions.length, zoneRegions[0].length);

        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return Configurator.getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/zones.xsd");
    }

    /**
     * Gets the settings.
     *
     * @param name the name
     * @return the settings
     */
    public static AbstractZoneSettings getSettings(String name) {
        return SETTINGS.get(name);
    }


    /**
     * Reload.
     */
    public void reload() {
        // Unload zones.
        unload();

        // Load the zones.
        load();

        // Re-validate all characters in zones.
        for (WorldObject obj : World.getInstance().getVisibleObjects()) {
            if (isCreature(obj)) {
                ((Creature) obj).revalidateZone(true);
            }
        }

        SETTINGS.clear();
    }

    public void unload() {
        // Get the world regions
        int count = 0;

        // Backup old zone settings
        for (IntMap<? extends Zone> map : _classZones.values()) {
            for (Zone zone : map.values()) {
                if (zone.getSettings() != null) {
                    SETTINGS.put(zone.getName(), zone.getSettings());
                }
            }
        }

        // Clear zones
        for (ZoneRegion[] zoneRegions : zoneRegions) {
            for (ZoneRegion zoneRegion : zoneRegions) {
                zoneRegion.getZones().clear();
                count++;
            }
        }
        LOGGER.info(getClass().getSimpleName() + ": Removed zones in " + count + " regions.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        Node attribute;
        String zoneName;
        int[][] coords;
        int zoneId;
        int minZ;
        int maxZ;
        String zoneType;
        String zoneShape;
        final List<int[]> rs = new ArrayList<>();

        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                attrs = n.getAttributes();
                attribute = attrs.getNamedItem("enabled");
                if ((attribute != null) && !Boolean.parseBoolean(attribute.getNodeValue())) {
                    continue;
                }

                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("zone".equalsIgnoreCase(d.getNodeName())) {
                        attrs = d.getAttributes();

                        attribute = attrs.getNamedItem("type");
                        if (attribute != null) {
                            zoneType = attribute.getNodeValue();
                        } else {
                            LOGGER.warn("ZoneData: Missing type for zone in file: " + f.getName());
                            continue;
                        }

                        attribute = attrs.getNamedItem("id");
                        if (attribute != null) {
                            zoneId = Integer.parseInt(attribute.getNodeValue());
                        } else {
                            zoneId = zoneType.equalsIgnoreCase("NpcSpawnTerritory") ? 0 : _lastDynamicId++;
                        }

                        attribute = attrs.getNamedItem("name");
                        if (attribute != null) {
                            zoneName = attribute.getNodeValue();
                        } else {
                            zoneName = null;
                        }

                        // Check zone name for NpcSpawnTerritory. Must exist and to be unique
                        if (zoneType.equalsIgnoreCase("NpcSpawnTerritory")) {
                            if (zoneName == null) {
                                LOGGER.warn("ZoneData: Missing name for NpcSpawnTerritory in file: " + f.getName() + ", skipping zone");
                                continue;
                            } else if (_spawnTerritories.containsKey(zoneName)) {
                                LOGGER.warn("ZoneData: Name " + zoneName + " already used for another zone, check file: " + f.getName() + ". Skipping zone");
                                continue;
                            }
                        }

                        minZ = parseInteger(attrs, "minZ");
                        maxZ = parseInteger(attrs, "maxZ");

                        zoneType = parseString(attrs, "type");
                        zoneShape = parseString(attrs, "shape");

                        // Get the zone shape from xml
                        ZoneForm zoneForm = null;
                        try {
                            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                if ("node".equalsIgnoreCase(cd.getNodeName())) {
                                    attrs = cd.getAttributes();
                                    final int[] point = new int[2];
                                    point[0] = parseInteger(attrs, "X");
                                    point[1] = parseInteger(attrs, "Y");
                                    rs.add(point);
                                }
                            }

                            coords = rs.toArray(new int[rs.size()][2]);
                            rs.clear();

                            if (coords.length == 0) {
                                LOGGER.warn(getClass().getSimpleName() + ": ZoneData: missing data for zone: " + zoneId + " XML file: " + f.getName());
                                continue;
                            }

                            // Create this zone. Parsing for cuboids is a bit different than for other polygons cuboids need exactly 2 points to be defined.
                            // Other polygons need at least 3 (one per vertex)
                            if (zoneShape.equalsIgnoreCase("Cuboid")) {
                                if (coords.length == 2) {
                                    zoneForm = new ZoneCuboid(coords[0][0], coords[1][0], coords[0][1], coords[1][1], minZ, maxZ);
                                } else {
                                    LOGGER.warn(getClass().getSimpleName() + ": ZoneData: Missing cuboid vertex data for zone: " + zoneId + " in file: " + f.getName());
                                    continue;
                                }
                            } else if (zoneShape.equalsIgnoreCase("NPoly")) {
                                // nPoly needs to have at least 3 vertices
                                if (coords.length > 2) {
                                    final int[] aX = new int[coords.length];
                                    final int[] aY = new int[coords.length];
                                    for (int i = 0; i < coords.length; i++) {
                                        aX[i] = coords[i][0];
                                        aY[i] = coords[i][1];
                                    }
                                    zoneForm = new ZoneNPoly(aX, aY, minZ, maxZ);
                                } else {
                                    LOGGER.warn(getClass().getSimpleName() + ": ZoneData: Bad data for zone: " + zoneId + " in file: " + f.getName());
                                    continue;
                                }
                            } else if (zoneShape.equalsIgnoreCase("Cylinder")) {
                                // A Cylinder zone requires a center point
                                // at x,y and a radius
                                attrs = d.getAttributes();
                                final int zoneRad = Integer.parseInt(attrs.getNamedItem("rad").getNodeValue());
                                if ((coords.length == 1) && (zoneRad > 0)) {
                                    zoneForm = new ZoneCylinder(coords[0][0], coords[0][1], minZ, maxZ, zoneRad);
                                } else {
                                    LOGGER.warn(getClass().getSimpleName() + ": ZoneData: Bad data for zone: " + zoneId + " in file: " + f.getName());
                                    continue;
                                }
                            } else {
                                LOGGER.warn(getClass().getSimpleName() + ": ZoneData: Unknown shape: \"" + zoneShape + "\"  for zone: " + zoneId + " in file: " + f.getName());
                                continue;
                            }
                        } catch (Exception e) {
                            LOGGER.warn(getClass().getSimpleName() + ": ZoneData: Failed to load zone " + zoneId + " coordinates: " + e.getMessage(), e);
                        }

                        // No further parameters needed, if NpcSpawnTerritory is loading
                        if (zoneType.equalsIgnoreCase("NpcSpawnTerritory")) {
                            _spawnTerritories.put(zoneName, new SpawnTerritory(zoneName, zoneForm));
                            continue;
                        }

                        // Create the zone
                        Class<?> newZone;
                        Constructor<?> zoneConstructor;
                        Zone temp;
                        try {
                            newZone = Class.forName("org.l2j.gameserver.world.zone.type." + zoneType);
                            zoneConstructor = newZone.getConstructor(int.class);
                            temp = (Zone) zoneConstructor.newInstance(zoneId);
                            temp.setZone(zoneForm);
                        } catch (Exception e) {
                            LOGGER.warn(getClass().getSimpleName() + ": ZoneData: No such zone type: " + zoneType + " in file: " + f.getName());
                            continue;
                        }

                        // Check for additional parameters
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("stat".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                final String name = attrs.getNamedItem("name").getNodeValue();
                                final String val = attrs.getNamedItem("val").getNodeValue();

                                temp.setParameter(name, val);
                            } else if ("spawn".equalsIgnoreCase(cd.getNodeName()) && (temp instanceof ZoneRespawn)) {
                                attrs = cd.getAttributes();
                                final int spawnX = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
                                final int spawnY = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
                                final int spawnZ = Integer.parseInt(attrs.getNamedItem("Z").getNodeValue());
                                final Node val = attrs.getNamedItem("type");
                                ((ZoneRespawn) temp).parseLoc(spawnX, spawnY, spawnZ, val == null ? null : val.getNodeValue());
                            } else if ("race".equalsIgnoreCase(cd.getNodeName()) && (temp instanceof RespawnZone)) {
                                attrs = cd.getAttributes();
                                final String race = attrs.getNamedItem("name").getNodeValue();
                                final String point = attrs.getNamedItem("point").getNodeValue();

                                ((RespawnZone) temp).addRaceRespawnPoint(race, point);
                            }
                        }
                        if (checkId(zoneId)) {
                            LOGGER.warn("Caution: Zone ({}) from file: {} overrides previos definition.", zoneId, f.getName());
                        }

                        if ((zoneName != null) && !zoneName.isEmpty()) {
                            temp.setName(zoneName);
                        }

                        addZone(zoneId, temp);

                        // Register the zone into any world region it
                        // intersects with...
                        // currently 11136 test for each zone :>
                        for (int x = 0; x < zoneRegions.length; x++) {
                            for (int y = 0; y < zoneRegions[x].length; y++) {

                                final int ax = (x - OFFSET_X) << SHIFT_BY;
                                final int bx = ((x + 1) - OFFSET_X) << SHIFT_BY;
                                final int ay = (y - OFFSET_Y) << SHIFT_BY;
                                final int by = ((y + 1) - OFFSET_Y) << SHIFT_BY;

                                if (temp.getZone().intersectsRectangle(ax, bx, ay, by)) {
                                    zoneRegions[x][y].getZones().put(temp.getId(), temp);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public final void load() {
        _classZones.clear();
        _spawnTerritories.clear();
        parseDatapackDirectory("data/zones", true);
        LOGGER.info("Loaded {} zone classes and {} zones.", _classZones.size(), getSize());
        LOGGER.info("Loaded {}  NPC spawn territoriers.", _spawnTerritories.size());
        final OptionalInt maxId = _classZones.values().stream().flatMapToInt(map -> map.keySet().stream()).filter(value -> value < 300000).max();
        maxId.ifPresent(id -> LOGGER.info("Last static id: {}", id));
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        int i = 0;
        for (IntMap<? extends Zone> map : _classZones.values()) {
            i += map.size();
        }
        return i;
    }

    /**
     * Check id.
     *
     * @param id the id
     * @return true, if successful
     */
    private boolean checkId(int id) {
        for (IntMap<? extends Zone> map : _classZones.values()) {
            if (map.containsKey(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add new zone.
     *
     * @param <T>  the generic type
     * @param id   the id
     * @param zone the zone
     */
    @SuppressWarnings("unchecked")
    private <T extends Zone> void addZone(Integer id, T zone) {
        IntMap<T> map = (IntMap<T>) _classZones.get(zone.getClass());
        if (map == null) {
            map = new HashIntMap<>();
            map.put(id, zone);
            _classZones.put(zone.getClass(), map);
        } else {
            map.put(id, zone);
        }
    }

    /**
     * Return all zones by class type.
     *
     * @param <T>      the generic type
     * @param zoneType Zone class
     * @return Collection of zones
     */
    @SuppressWarnings("unchecked")
    public <T extends Zone> Collection<T> getAllZones(Class<T> zoneType) {
        return (Collection<T>) _classZones.get(zoneType).values();
    }

    /**
     * Get zone by ID.
     *
     * @param id the id
     * @return the zone by id
     * @see #getZoneById(int, Class)
     */
    public Zone getZoneById(int id) {
        for (IntMap<? extends Zone> map : _classZones.values()) {
            if (map.containsKey(id)) {
                return map.get(id);
            }
        }
        return null;
    }

    /**
     * Get zone by name.
     *
     * @param name the zone name
     * @return the zone by name
     */
    public Zone getZoneByName(String name) {
        for (IntMap<? extends Zone> map : _classZones.values()) {
            final Optional<? extends Zone> zoneType = map.values().stream().filter(z -> (z.getName() != null) && z.getName().equals(name)).findAny();
            if (zoneType.isPresent()) {
                return zoneType.get();
            }
        }
        return null;
    }

    /**
     * Get zone by ID and zone class.
     *
     * @param <T>      the generic type
     * @param id       the id
     * @param zoneType the zone type
     * @return zone
     */
    @SuppressWarnings("unchecked")
    public <T extends Zone> T getZoneById(int id, Class<T> zoneType) {
        return (T) _classZones.get(zoneType).get(id);
    }

    /**
     * Returns all zones from where the object is located.
     *
     * @param locational the locational
     * @return zones
     */
    public List<Zone> getZones(ILocational locational) {
        return getZones(locational.getX(), locational.getY(), locational.getZ());
    }

    /**
     * Gets the zone.
     *
     * @param <T>        the generic type
     * @param locational the locational
     * @param type       the type
     * @return zone from where the object is located by type
     */
    public <T extends Zone> T getZone(ILocational locational, Class<T> type) {
        return isNull(locational) ?  null : getZone(locational.getX(), locational.getY(), locational.getZ(), type);
    }

    /**
     * Returns all zones from given coordinates (plane).
     *
     * @param x the x
     * @param y the y
     * @return zones
     */
    public List<Zone> getZones(int x, int y) {
        var region = getRegion(x, y);
        return isNull(region) ? Collections.emptyList() : region.getZones().values().stream().filter(z -> z.isInsideZone(x, y)).collect(Collectors.toList());
    }

    /**
     * Returns all zones from given coordinates.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return zones
     */
    public List<Zone> getZones(int x, int y, int z) {
        final List<Zone> temp = new ArrayList<>();
        for (Zone zone : getRegion(x, y).getZones().values()) {
            if (zone.isInsideZone(x, y, z)) {
                temp.add(zone);
            }
        }
        return temp;
    }

    /**
     * Gets the zone.
     *
     * @param <T>  the generic type
     * @param x    the x
     * @param y    the y
     * @param z    the z
     * @param type the type
     * @return zone from given coordinates
     */
    private <T extends Zone> T getZone(int x, int y, int z, Class<T> type) {
        var region = getRegion(x, y);
        if(nonNull(region)) {
            return region.getZones().values().stream().filter(zone -> type.isInstance(zone) && zone.isInsideZone(x, y, z)).map(type::cast).findFirst().orElse(null);
        }
        return null;
    }

    /**
     * Get spawm territory by name
     *
     * @param name name of territory to search
     * @return link to zone form
     */
    public SpawnTerritory getSpawnTerritory(String name) {
        return _spawnTerritories.getOrDefault(name, null);
    }

    /**
     * Returns all spawm territories from where the object is located
     *
     * @param object
     * @return zones
     */
    public List<SpawnTerritory> getSpawnTerritories(WorldObject object) {
        final List<SpawnTerritory> temp = new ArrayList<>();
        for (SpawnTerritory territory : _spawnTerritories.values()) {
            if (territory.isInsideZone(object.getX(), object.getY(), object.getZ())) {
                temp.add(territory);
            }
        }

        return temp;
    }

    /**
     * Gets the olympiad stadium.
     *
     * @param character the character
     * @return the olympiad stadium
     */
    public final OlympiadStadiumZone getOlympiadStadium(Creature character) {
        if (character == null) {
            return null;
        }

        for (Zone temp : getInstance().getZones(character.getX(), character.getY(), character.getZ())) {
            if ((temp instanceof OlympiadStadiumZone) && temp.isCharacterInZone(character)) {
                return (OlympiadStadiumZone) temp;
            }
        }
        return null;
    }

    /**
     * General storage for debug items used for visualizing zones.
     *
     * @return list of items
     */
    public List<Item> getDebugItems() {
        if (_debugItems == null) {
            _debugItems = new ArrayList<>();
        }
        return _debugItems;
    }

    /**
     * Remove all debug items from l2world.
     */
    public void clearDebugItems() {
        if (_debugItems != null) {
            final Iterator<Item> it = _debugItems.iterator();
            while (it.hasNext()) {
                final Item item = it.next();
                if (item != null) {
                    item.decayMe();
                }
                it.remove();
            }
        }
    }

    public ZoneRegion getRegion(int x, int y) {
        try {
            return zoneRegions[(x >> SHIFT_BY) + OFFSET_X][(y >> SHIFT_BY) + OFFSET_Y];
        } catch (ArrayIndexOutOfBoundsException e) {
            // LOGGER.warn(getClass().getSimpleName() + ": Incorrect zone region X: " + ((x >> SHIFT_BY) + OFFSET_X) + " Y: " + ((y >> SHIFT_BY) + OFFSET_Y) + " for coordinates x: " + x + " y: " + y);
            return null;
        }
    }

    public ZoneRegion getRegion(ILocational point) {
        return getRegion(point.getX(), point.getY());
    }

    public static ZoneManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ZoneManager INSTANCE = new ZoneManager();
    }
}
