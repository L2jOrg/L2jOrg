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
package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.ArrayIntList;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntList;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.form.ZoneCubeArea;
import org.l2j.gameserver.world.zone.form.ZoneCylinderArea;
import org.l2j.gameserver.world.zone.form.ZonePolygonArea;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.l2j.gameserver.world.zone.type.SpawnZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * This class manages the zones
 *
 * @author durgus
 * @author JoeAlisson
 */
public final class ZoneEngine extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneEngine.class);
    private static final Map<String, AbstractZoneSettings> SETTINGS = new HashMap<>();

    private static final int SHIFT_BY = 15;
    private static final int OFFSET_X = Math.abs(World.MAP_MIN_X >> SHIFT_BY);
    private static final int OFFSET_Y = Math.abs(World.MAP_MIN_Y >> SHIFT_BY);
    public static final String MIN_Z = "min-z";
    public static final String MAX_Z = "max-z";

    private final Map<Class<? extends Zone>, IntMap<? extends Zone>> classZones = new HashMap<>();
    private final Map<String, SpawnTerritory> spawnTerritories = new HashMap<>();
    private final Map<String, ZoneFactory> factories = new HashMap<>();
    private final ZoneRegion[][] zoneRegions;
    private int lastDynamicId = 300000;

    private ZoneEngine() {
        var regionsX = (World.MAP_MAX_X >> SHIFT_BY) + OFFSET_X + 1;
        var regionsY = (World.MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y + 1;
        zoneRegions = new ZoneRegion[regionsX][regionsY];
        for (var x = 0; x < regionsX; x++) {
            for (var y = 0; y < regionsY; y++) {
                zoneRegions[x][y] = new ZoneRegion();
            }
        }
        LOGGER.info("Zone Region Grid set up: {} by {}", regionsX, regionsY);
    }

    private void registerFactory(ZoneFactory zoneFactory) {
        factories.put(zoneFactory.type(), zoneFactory);
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/zones/zones.xsd");
    }

    @Override
    public final void load() {
        classZones.clear();
        spawnTerritories.clear();
        parseDatapackDirectory("data/zones", true);

        LOGGER.info("Loaded {} zone classes and {} zones.", classZones.size(), getSize());
        LOGGER.info("Loaded {} NPC spawn territories.", spawnTerritories.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File file) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName()) && parseBoolean(n.getAttributes(), "enabled")) {
                parseZoneList(n, file);
            }
        }
    }

    private void parseZoneList(Node n, File file) {
        for(var zoneNode = n.getFirstChild(); nonNull(zoneNode); zoneNode = zoneNode.getNextSibling()) {
            try {
                var nodeName= zoneNode.getNodeName();
                if(nodeName.equals("territory")) {
                    addTerritory(zoneNode, file.getAbsolutePath());
                } else {
                    var factory = factories.get(zoneNode.getNodeName());
                    if (nonNull(factory)) {
                        parseZone(zoneNode, factory);
                    } else {
                        LOGGER.warn("There is no factory registered to zone type {} in file {}", zoneNode.getNodeName(), file);
                    }
                }
            } catch (InvalidZoneException e) {
                LOGGER.warn("Could not parse zone type {} in file {}", zoneNode.getNodeName(), file);
            }
        }
    }

    private void parseZone(Node zoneNode, ZoneFactory factory) throws InvalidZoneException {
        var attributes = zoneNode.getAttributes();
        if(parseBoolean(attributes, "enabled")) {
            int id = zoneId(zoneNode.getAttributes());

            var zone = factory.create(id, zoneNode, this);
            zone.setName(parseString(attributes, "name"));
            parseZoneProperties(zoneNode, zone);
            addZone(id, zone);
            registerIntoWorldRegion(zone);
        }
    }

    private int zoneId(NamedNodeMap attributes) {
        var idNode = attributes.getNamedItem("id");
        return idNode == null ? lastDynamicId++ : parseInt(idNode);
    }

    private void parseZoneProperties(Node zoneNode, Zone zone) throws InvalidZoneException {
        for (var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            var attr = node.getAttributes();
            switch (node.getNodeName()) {
                case "polygon" -> zone.setArea(parsePolygon(node));
                case "cube" -> zone.setArea(parseCube(node));
                case "cylinder" -> zone.setArea(parseCylinder(node));
                case "spawn" -> parseSpawn(zone, attr);
                case "respawn" -> parseRespawn(zone, attr);
            }
        }
    }

    public int addCylinderZone(Class<?> zoneClass,String zoneName, Location coords, int radius) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        var constructor = zoneClass.asSubclass(Zone.class).getConstructor(int.class);

        int zoneId = lastDynamicId++;

        ZoneArea area = new ZoneCylinderArea(coords.getX(), coords.getY(), coords.getZ() - 100, coords.getZ() + 100, radius);

        var zone = constructor.newInstance(zoneId);
        zone.setName(zoneName);
        zone.setArea(area);

        if(isNull(zone.getArea())) {
            LOGGER.error("There is no defined area to Zone {}", zone);
        }

        if(nonNull(addZone(zoneId, zone))) {
            LOGGER.warn("Zone ({}) overrides previous definition.", zone);
        }
        registerIntoWorldRegion(zone);

        return zoneId;
    }

    private void registerIntoWorldRegion(Zone zone) {
        // Register the zone into any world region it
        // intersects with...
        // currently 11136 test for each zone :>
        for (var x = 0; x < zoneRegions.length; x++) {
            for (var y = 0; y < zoneRegions[x].length; y++) {

                final var ax = (x - OFFSET_X) << SHIFT_BY;
                final var bx = ((x + 1) - OFFSET_X) << SHIFT_BY;
                final var ay = (y - OFFSET_Y) << SHIFT_BY;
                final var by = ((y + 1) - OFFSET_Y) << SHIFT_BY;

                if (zone.getArea().intersectsRectangle(ax, bx, ay, by)) {
                    zoneRegions[x][y].registerZone(zone);
                }
            }
        }
    }

    private void parseRespawn(Zone zone, NamedNodeMap attr) {
        if(zone instanceof RespawnZone respawnZone) {
            var race = parseString(attr, "race");
            final var region = parseString(attr,"region");
            respawnZone.addRaceRespawnPoint(race, region);
        }
    }

    private void parseSpawn(Zone zone, NamedNodeMap attr) {
        if(zone instanceof SpawnZone spawnZone) {
            var x = parseInt(attr, "x");
            var y = parseInt(attr, "y");
            var z = parseInt(attr, "z");
            var type = parseString(attr, "type");
            spawnZone.parseLoc(x, y, z, type);
        }
    }

    private void addTerritory(Node zoneNode, String file) throws InvalidZoneException {
        var name = parseString(zoneNode.getAttributes(), "name");
        if(isNullOrEmpty(name)) {
            LOGGER.warn("Missing name for SpawnTerritory in file: {}, skipping zone", file);
        } else if (spawnTerritories.containsKey(name)) {
            LOGGER.warn("Spawn Territory Name {} already used for another zone, check file: {}. Skipping zone", name, file);
        } else {

            ZoneArea area = null;

            for (var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {

                area = switch (node.getNodeName()) {
                    case "polygon" -> parsePolygon(node);
                    case "cube" -> parseCube(node);
                    case "cylinder" -> parseCylinder(node);
                    default -> null;
                };

                if(nonNull(area)) {
                    spawnTerritories.put(name, new SpawnTerritory(name, area));
                    break;
                }
            }
            if(isNull(area)) {
                LOGGER.warn("There is no defined area to Spawn Territory {} on file {}", name, file);
            }
        }
    }

    private ZoneArea parsePolygon(Node polygonNode) throws InvalidZoneException {
        IntList xPoints = new ArrayIntList();
        IntList yPoints = new ArrayIntList();

        for (var node = polygonNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (isPointNode(node)) {
                var attr = node.getAttributes();
                xPoints.add(parseInt(attr, "x"));
                yPoints.add(parseInt(attr, "y"));
            }
        }
        if(xPoints.size() < 3) {
            throw new InvalidZoneException("The Zone with Polygon form must have at least 3 points");
        }

        var attributes = polygonNode.getAttributes();
        var minZ = parseInt(attributes, MIN_Z);
        var maxZ = parseInt(attributes, MAX_Z);
        return new ZonePolygonArea(xPoints.toArray(int[]::new), yPoints.toArray(int[]::new), minZ, maxZ);
    }

    private boolean isPointNode(Node node) {
        return "point".equalsIgnoreCase(node.getNodeName());
    }

    private ZoneArea parseCylinder(Node zoneNode) throws InvalidZoneException {
        var attributes = zoneNode.getAttributes();
        var radius = parseInt(attributes, "radius");

        if(radius <= 0) {
            throw new  InvalidZoneException("The Zone with Cylinder form must have a radius");
        }

        for (var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (isPointNode(node)) {
                var attr = node.getAttributes();

                var x = parseInt(attr, "x");
                var y = parseInt(attr, "y");
                var minZ = parseInt(attributes, MIN_Z);
                var maxZ = parseInt(attributes, MAX_Z);
                return new ZoneCylinderArea(x, y, minZ, maxZ, radius);
            }
        }
        throw new InvalidZoneException("The Zone with Cylinder form must have 1 point");
    }

    private ZoneArea parseCube(Node cubeNode) throws InvalidZoneException {
        var attributes = cubeNode.getAttributes();
        var points = new int[4];
        var point = 0;
        for (var node = cubeNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (isPointNode(node)) {
                var attr = node.getAttributes();

                var x = parseInt(attr, "x");
                var y = parseInt(attr, "y");
                points[point++] = x;
                points[point++] = y;

                if(point > 3) {
                    var minZ = parseInt(attributes, MIN_Z);
                    var maxZ = parseInt(attributes, MAX_Z);
                    return new ZoneCubeArea(points[0], points[2], points[1], points[3], minZ, maxZ);
                }
            }
        }
        throw new InvalidZoneException("The Zone with Cube Form must have 2 points");
    }

    public void reload() {
        unload();
        load();

        World.getInstance().forEachCreature(creature -> creature.revalidateZone(true));
        SETTINGS.clear();
    }

    public void unload() {
        for (IntMap<? extends Zone> zones : classZones.values()) {
            for (Zone zone : zones.values()) {
                if(nonNull(zone.getSettings())) {
                    SETTINGS.put(zone.getName(), zone.getSettings());
                }
            }
        }

        for (ZoneRegion[] regions : zoneRegions) {
            for (ZoneRegion region : regions) {
                region.clear();
            }
        }
        LOGGER.info("Removed zones in regions.");
    }


    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        var size = 0;
        for (IntMap<? extends Zone> zones : classZones.values()) {
            size += zones.size();
        }
        return size;
    }

    /**
     * Add new zone.
     *  @param <T>  the generic type
     * @param id   the id
     * @param zone the zone
     * @return the old zone related to id
     */
    @SuppressWarnings("unchecked")
    private <T extends Zone> T addZone(int id, T zone) {
        return ((IntMap<T>) classZones.computeIfAbsent(zone.getClass(), k -> new HashIntMap<T>())).put(id, zone);
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
     * Get zone by name.
     *
     * @param name the zone name
     * @return the zone by name
     */
    public Zone getZoneByName(String name) {
        for (IntMap<? extends Zone> zones : classZones.values()) {
            for (Zone zone : zones.values()) {
                if(Objects.equals(name, zone.getName())) {
                    return zone;
                }
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

    public Zone getZoneById(int id) {
        for (IntMap<? extends Zone> zones : classZones.values()) {
            if(zones.containsKey(id)) {
                return zones.get(id);
            }
        }
        return null;
    }

    public <T extends Zone> T findFirstZone(int x, int y, Class<T> zoneClass) {
        var region = getRegion(x, y);
        if(region != null) {
            for (Zone zone : region.getZones()) {
                if(zoneClass.isInstance(zone) && zone.isInsideZone(x, y)) {
                    return zoneClass.cast(zone);
                }
            }
        }
        return null;
    }

    public <T extends Zone> T findFirstZone(ILocational loc, Class<T> zoneClass) {
        return findFirstZone(loc.getX(), loc.getY(), loc.getZ(), zoneClass);
    }

    public <T extends Zone> T findFirstZone(int x, int y, int z, Class<T> zoneClass) {
        var region = getRegion(x, y);
        if(region != null) {
            for (Zone zone : region.getZones()) {
                if(zoneClass.isInstance(zone) && zone.isInsideZone(x, y, z)) {
                    return zoneClass.cast(zone);
                }
            }
        }
        return null;
    }

    public void forEachZone(ILocational loc, Consumer<Zone> action) {
        forEachZone(loc.getX(), loc.getY(), loc.getZ(), Zone.class, action);
    }

    public <T extends Zone> void forEachZone(ILocational loc, Class<T> zoneClass, Consumer<T> action) {
        forEachZone(loc.getX(), loc.getY(), loc.getZ(), zoneClass, action);
    }

    public <T extends Zone> void forEachZone(int x, int y, int z, Class<T> zoneClass, Consumer<T> action) {
        var region = getRegion(x, y);
        if(region != null) {
            for (Zone zone : region.getZones()) {
                if(zoneClass.isInstance(zone) && zone.isInsideZone(x, y, z)) {
                    action.accept(zoneClass.cast(zone));
                }
            }
        }
    }

    public boolean anyZoneMatches(ILocational loc, Predicate<Zone> predicate) {
        var region = getRegion(loc.getX(), loc.getY());
        if(region != null) {
            for (Zone zone : region.getZones()) {
                if(zone.isInsideZone(loc) && predicate.test(zone)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get spawm territory by name
     *
     * @param name name of territory to search
     * @return link to zone form
     */
    public SpawnTerritory getSpawnTerritory(String name) {
        return spawnTerritories.get(name);
    }

    /**
     * Returns all spawm territories from where the object is located
     *
     * @param object the reference object
     * @return zones
     */
    public List<SpawnTerritory> getSpawnTerritories(WorldObject object) {
        return spawnTerritories.values().stream().filter(t -> t.isInsideZone(object.getX(), object.getY(), object.getZ())).collect(Collectors.toList());
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

    public void removeFromZones(Creature creature) {
        var region = getRegion(creature);
        if(region != null) {
            region.removeFromZones(creature);
        }
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

    public static void init() {
        var engine = getInstance();
        ServiceLoader.load(ZoneFactory.class).forEach(engine::registerFactory);
        engine.load();
    }

    public static ZoneEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ZoneEngine INSTANCE = new ZoneEngine();
    }
}
