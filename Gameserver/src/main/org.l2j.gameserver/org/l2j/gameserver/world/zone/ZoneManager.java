package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.lists.IntList;
import io.github.joealisson.primitive.lists.impl.ArrayIntList;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.form.ZoneCubeForm;
import org.l2j.gameserver.world.zone.form.ZoneCylinderForm;
import org.l2j.gameserver.world.zone.form.ZonePolygonForm;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * This class manages the zones
 *
 * @author durgus
 * @author joeAlisson
 */
public final class ZoneManager extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneManager.class);
    private static final Map<String, AbstractZoneSettings> SETTINGS = new HashMap<>();

    private static final int SHIFT_BY = 15;
    private static final int OFFSET_X = Math.abs(World.MAP_MIN_X >> SHIFT_BY);
    private static final int OFFSET_Y = Math.abs(World.MAP_MIN_Y >> SHIFT_BY);

    private final Map<Class<? extends Zone>, IntMap<? extends Zone>> classZones = new HashMap<>();
    private final Map<String, SpawnTerritory> spawnTerritories = new HashMap<>();
    private final ZoneRegion[][] zoneRegions;
    private int lastDynamicId = 300000;
    private List<Item> _debugItems;

    private ZoneManager() {
        var regionsX = (World.MAP_MAX_X >> SHIFT_BY) + OFFSET_X + 1;
        var regionsY = (World.MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y + 1;
        zoneRegions = new ZoneRegion[regionsX][regionsY];
        for (int x = 0; x < regionsX; x++) {
            for (int y = 0; y < regionsY; y++) {
                zoneRegions[x][y] = new ZoneRegion(x, y);
            }
        }
        LOGGER.info("Zone Region Grid set up: {} by {}", regionsX, regionsY);

        load();
    }

    @Override
    public final void load() {
        classZones.clear();
        spawnTerritories.clear();
        parseDatapackDirectory("data/zones", true);
        LOGGER.info("Loaded {} zone classes and {} zones.", classZones.size(), getSize());
        LOGGER.info("Loaded {}  NPC spawn territories.", spawnTerritories.size());
        final OptionalInt maxId = classZones.values().stream().flatMapToInt(map -> map.keySet().stream()).filter(value -> value < 300000).max();
        maxId.ifPresent(id -> LOGGER.info("Last static id: {}", id));
    }

    @Override
    protected Path getSchemaFilePath() {
        return Configurator.getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/zones.xsd");
    }

    /**
     * Reload.
     */
    public void reload() {
        unload();
        load();

        World.getInstance().forEachCreature(creature -> creature.revalidateZone(true));

        SETTINGS.clear();
    }

    public void unload() {
        // Backup old zone settings
        classZones.values().stream()
                .flatMap(map -> map.values().stream())
                .filter(z -> nonNull(z.getSettings()))
                .forEach(z -> SETTINGS.put(z.getName(), z.getSettings()));

        Arrays.stream(zoneRegions).flatMap(Arrays::stream).forEach(r -> r.getZones().clear());
        LOGGER.info("Removed zones in regions.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName()) &&  parseBoolean(n.getAttributes(), "enabled")) {
                forEach(n, "zone", zone -> this.parseZone(zone, f.getAbsolutePath()));
            }
        }
    }

    private void parseZone(Node zoneNode, String file) {
        final var attributes = zoneNode.getAttributes();

        var type = parseString(attributes, "type");
        try {
            var zoneClass = Class.forName(type);

            if(SpawnTerritory.class.isAssignableFrom(zoneClass)) {
                addTerritory(zoneNode, file);
                return;
            }

            if(!Zone.class.isAssignableFrom(zoneClass)) {
                LOGGER.warn("The zone type: {} in file: {} is not subclass of Zone Class", type, file);
                return;
            }

            addZone(zoneNode, zoneClass, file);
        } catch (ClassNotFoundException e) {
            LOGGER.warn("No such zone type: {} in file: {}", type, file);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOGGER.warn("The type: {} in file: {} must have a public constructor with a int parameter", type, file);
        } catch (InvalidZoneException e) {
            LOGGER.warn("There is a invalid Zone in file {}: {}", file, e.getMessage());
        }
    }

    private void addZone(Node zoneNode, Class<?> zoneClass, String file) throws NoSuchMethodException, InvalidZoneException, InstantiationException, IllegalAccessException, InvocationTargetException {
        var attributes = zoneNode.getAttributes();
        var constructor = zoneClass.asSubclass(Zone.class).getConstructor(int.class);
        ZoneForm form = parseZoneForm(zoneNode);

        var zoneId = parseInteger(attributes, "id");
        if(isNull(zoneId)) {
            zoneId = lastDynamicId++;
        }

        var zone = constructor.newInstance(zoneId);
        zone.setForm(form);
        zone.setName(parseString(attributes, "name"));

        parseZoneProperties(zoneNode, zone);

        if (checkId(zoneId)) {
            LOGGER.warn("Zone ({}) from file: {} overrides previous definition.", zoneId, file);
        }

        addZone(zoneId, zone);
        registerIntoWorldRegion(zone);

    }

    private void registerIntoWorldRegion(Zone zone) {
        // Register the zone into any world region it
        // intersects with...
        // currently 11136 test for each zone :>
        for (int x = 0; x < zoneRegions.length; x++) {
            for (int y = 0; y < zoneRegions[x].length; y++) {

                final int ax = (x - OFFSET_X) << SHIFT_BY;
                final int bx = ((x + 1) - OFFSET_X) << SHIFT_BY;
                final int ay = (y - OFFSET_Y) << SHIFT_BY;
                final int by = ((y + 1) - OFFSET_Y) << SHIFT_BY;

                if (zone.getForm().intersectsRectangle(ax, bx, ay, by)) {
                    zoneRegions[x][y].getZones().put(zone.getId(), zone);
                }
            }
        }
    }

    private void parseZoneProperties(Node zoneNode, Zone zone) {
        for (Node cd = zoneNode.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
            var attr = cd.getAttributes();

            if ("property".equalsIgnoreCase(cd.getNodeName())) {
                final String name = parseString(attr, "name");
                final String value = parseString(attr, "value");

                zone.setParameter(name, value);
            } else if ("spawn".equalsIgnoreCase(cd.getNodeName()) && (zone instanceof ZoneRespawn)) {
                final int spawnX = parseInteger(attr, "x");
                final int spawnY = parseInteger(attr, "y");
                final int spawnZ = parseInteger(attr, "z");
                final String spawnType = parseString(attr, "type");
                ((ZoneRespawn) zone).parseLoc(spawnX, spawnY, spawnZ, spawnType);
            } else if ("respawn".equalsIgnoreCase(cd.getNodeName()) && (zone instanceof RespawnZone)) {
                final String race = parseString(attr, "race");
                final String point = parseString(attr,"region");
                ((RespawnZone) zone).addRaceRespawnPoint(race, point);
            }
        }
    }

    private void addTerritory(Node zoneNode, String file) throws InvalidZoneException {
        var name = parseString(zoneNode.getAttributes(), "name");
        if(isNullOrEmpty(name)) {
            LOGGER.warn("Missing name for SpawnTerritory in file: {}, skipping zone", file);
        } else if (spawnTerritories.containsKey(name)) {
            LOGGER.warn("Spawn Territory Name {} already used for another zone, check file: {}. Skipping zone", name, file);
        } else {
            spawnTerritories.put(name, new SpawnTerritory(name, parseZoneForm(zoneNode)));
        }
    }

    private ZoneForm parseZoneForm(Node zoneNode) throws InvalidZoneException {
        var form = parseString(zoneNode.getAttributes(), "form");
        return switch (form) {
            default -> null;
            case "Cube" -> parseCube(zoneNode);
            case "Cylinder" -> parseCylinder(zoneNode);
            case "Polygon" -> parsePolygon(zoneNode);
        };
    }

    private ZoneForm parsePolygon(Node zoneNode) throws InvalidZoneException {
        var attributes = zoneNode.getAttributes();
        IntList xPoints = new ArrayIntList();
        IntList yPoints = new ArrayIntList();

        for (Node node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("point".equalsIgnoreCase(node.getNodeName())) {
                var attr = node.getAttributes();
                xPoints.add(parseInteger(attr, "x"));
                yPoints.add(parseInteger(attr, "y"));
            }
        }
        if(xPoints.size() < 3) {
            throw new InvalidZoneException("The Zone with Polygon form must have at least 3 points");
        }

        var minZ = parseInteger(attributes, "minZ");
        var maxZ = parseInteger(attributes, "maxZ");
        return new ZonePolygonForm(xPoints.toArray(int[]::new), yPoints.toArray(int[]::new), minZ, maxZ);
    }

    private ZoneForm parseCylinder(Node zoneNode) throws InvalidZoneException {
        for (Node node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("point".equalsIgnoreCase(node.getNodeName())) {
                var attributes = node.getAttributes();
                int radius = parseInteger(attributes, "radius");

                if(radius < 0) {
                    throw new  InvalidZoneException("The Zone with Cylinder form must have a radius");
                }

                int x = parseInteger(attributes, "x");
                int y = parseInteger(attributes, "y");
                var minZ = parseInteger(attributes, "minZ");
                var maxZ = parseInteger(attributes, "maxZ");
                return new ZoneCylinderForm(x, y, minZ, maxZ, radius);
            }
        }
        throw new InvalidZoneException("The Zone with Cylinder form must have 1 point");
    }

    private ZoneForm parseCube(Node zoneNode) throws InvalidZoneException {
        int[] points = new int[4];
        var point = 0;
        for (Node node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("point".equalsIgnoreCase(node.getNodeName())) {
                var attributes = node.getAttributes();

                int x = parseInteger(attributes, "x");
                int y = parseInteger(attributes, "y");
                points[point++] = x;
                points[point++] = y;

                if(point > 3) {
                    var minZ = parseInteger(attributes, "minZ");
                    var maxZ = parseInteger(attributes, "maxZ");
                    return new ZoneCubeForm(points[0], points[2], points[1], points[3], minZ, maxZ);
                }
            }
        }
        throw new InvalidZoneException("The Zone with Cube Form must have 2 points");
    }


    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return classZones.values().stream().mapToInt(IntMap::size).sum();
    }

    /**
     * Check id.
     *
     * @param id the id
     * @return true, if successful
     */
    private boolean checkId(int id) {
        return classZones.values().stream().anyMatch(map -> map.containsKey(id));
    }

    /**
     * Add new zone.
     *
     * @param <T>  the generic type
     * @param id   the id
     * @param zone the zone
     */
    @SuppressWarnings("unchecked")
    private <T extends Zone> void addZone(int id, T zone) {
        IntMap<T> map = (IntMap<T>) classZones.get(zone.getClass());
        if (map == null) {
            map = new HashIntMap<>();
            map.put(id, zone);
            classZones.put(zone.getClass(), map);
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
        return (Collection<T>) classZones.get(zoneType).values();
    }

    /**
     * Get zone by ID.
     *
     * @param id the id
     * @return the zone by id
     * @see #getZoneById(int, Class)
     */
    public Zone getZoneById(int id) {
        for (IntMap<? extends Zone> map : classZones.values()) {
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
        for (IntMap<? extends Zone> map : classZones.values()) {
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
        return (T) classZones.get(zoneType).get(id);
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
        return spawnTerritories.getOrDefault(name, null);
    }

    /**
     * Returns all spawm territories from where the object is located
     *
     * @param object
     * @return zones
     */
    public List<SpawnTerritory> getSpawnTerritories(WorldObject object) {
        final List<SpawnTerritory> temp = new ArrayList<>();
        for (SpawnTerritory territory : spawnTerritories.values()) {
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
            LOGGER.warn("Incorrect zone region X: {} Y: {} for coordinates x: {} y:{}",  (x >> SHIFT_BY) + OFFSET_X, (y >> SHIFT_BY) + OFFSET_Y, x, y);
            return null;
        }
    }

    public ZoneRegion getRegion(ILocational point) {
        return getRegion(point.getX(), point.getY());
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

    public static ZoneManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ZoneManager INSTANCE = new ZoneManager();
    }
}
