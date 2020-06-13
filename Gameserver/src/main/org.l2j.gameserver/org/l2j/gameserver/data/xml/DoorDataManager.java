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
package org.l2j.gameserver.data.xml;

import io.github.joealisson.primitive.*;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.MapRegionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * This class loads and hold info about doors.
 *
 * @author JIV, GodKratos, UnAfraid
 * @author JoeAlisson
 */
public final class DoorDataManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoorDataManager.class);

    private final Map<String, IntSet> groups = new HashMap<>();
    private final IntMap<Door> doors = new HashIntMap<>();
    private final IntMap<StatsSet> templates = new HashIntMap<>();
    private final IntMap<List<Door>> regions = new HashIntMap<>();

    private DoorDataManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/doors.xsd");
    }

    @Override
    public void load() {
        doors.clear();
        groups.clear();
        regions.clear();
        parseDatapackFile("data/doors.xml");
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "door", doorNode -> spawnDoor(parseDoor(doorNode))));
        LOGGER.info("Loaded {} Door Templates for {} regions.", doors.size(), regions.size());
    }

    public StatsSet parseDoor(Node doorNode) {
        final StatsSet params = new StatsSet(parseAttributes(doorNode));
        params.set("baseHpMax", 1); // Avoid doors without HP value created dead due to default value 0 in CreatureTemplate

        forEach(doorNode, XmlReader::isNode, innerDoorNode -> {
            final NamedNodeMap attrs = innerDoorNode.getAttributes();
            if (innerDoorNode.getNodeName().equals("nodes")) {
                params.set("nodeZ", parseInteger(attrs, "nodeZ"));

                final AtomicInteger count = new AtomicInteger();
                forEach(innerDoorNode, XmlReader::isNode, nodes -> {
                    final NamedNodeMap nodeAttrs = nodes.getAttributes();
                    if ("node".equals(nodes.getNodeName())) {
                        params.set("nodeX_" + count.get(), parseInteger(nodeAttrs, "x"));
                        params.set("nodeY_" + count.getAndIncrement(), parseInteger(nodeAttrs, "y"));
                    }
                });
            } else if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    final Node att = attrs.item(i);
                    params.set(att.getNodeName(), att.getNodeValue());
                }
            }
        });

        applyCollisions(params);
        return params;
    }

    private void applyCollisions(StatsSet set) {
        if (set.contains("nodeX_0") && set.contains("nodeY_0") && set.contains("nodeX_1") && set.contains("nodeX_1")) {
            final int height = set.getInt("height", 150);
            final int nodeX = set.getInt("nodeX_0");
            final int nodeY = set.getInt("nodeY_0");
            final int posX = set.getInt("nodeX_1");
            final int posY = set.getInt("nodeX_1");
            int collisionRadius; // (max) radius for movement checks
            collisionRadius = Math.min(Math.abs(nodeX - posX), Math.abs(nodeY - posY));
            if (collisionRadius < 20) {
                collisionRadius = 20;
            }

            set.set("collision_radius", collisionRadius);
            set.set("collision_height", height);
        }
    }

    private void spawnDoor(StatsSet set) {
        final DoorTemplate template = new DoorTemplate(set);
        final Door door = spawnDoor(template, null);

        templates.put(door.getId(), set);
        doors.put(door.getId(), door);
        regions.computeIfAbsent(MapRegionManager.getInstance().getMapRegionLocId(door), key -> new ArrayList<>()).add(door);
    }

    /**
     * Spawns the door, adds the group name and registers it to templates
     *
     * @param template
     * @param instance
     * @return a new door instance based on provided template
     */
    public Door spawnDoor(DoorTemplate template, Instance instance) {
        final Door door = new Door(template);
        door.setCurrentHp(door.getMaxHp());

        if (instance != null) {
            door.setInstance(instance);
        }

        door.spawnMe(template.getX(), template.getY(), template.getZ());

        if (template.getGroupName() != null) {
            groups.computeIfAbsent(door.getGroupName(), key -> new HashIntSet()).add(door.getId());
        }
        return door;
    }

    public StatsSet getDoorTemplate(int doorId) {
        return templates.get(doorId);
    }

    public Door getDoor(int doorId) {
        return doors.get(doorId);
    }

    public IntSet getDoorsByGroup(String groupName) {
        return groups.getOrDefault(groupName, Containers.emptyIntSet());
    }

    public Collection<Door> getDoors() {
        return doors.values();
    }

    /**
     * GodKratos: TODO: remove GeoData checks from door table and convert door nodes to Geo zones
     */
    public boolean checkIfDoorsBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance, boolean doubleFaceCheck) {
        final Collection<Door> allDoors = (instance != null) ? instance.getDoors() : regions.get(MapRegionManager.getInstance().getMapRegionLocId(x, y));
        if (allDoors == null) {
            return false;
        }

        for (Door doorInst : allDoors) {
            // check dead and open
            if (doorInst.isDead() || doorInst.isOpen() || !doorInst.checkCollision() || (doorInst.getX(0) == 0)) {
                continue;
            }

            boolean intersectFace = false;
            for (int i = 0; i < 4; i++) {
                final int j = (i + 1) < 4 ? i + 1 : 0;
                // lower part of the multiplier fraction, if it is 0 we avoid an error and also know that the lines are parallel
                final int denominator = ((ty - y) * (doorInst.getX(i) - doorInst.getX(j))) - ((tx - x) * (doorInst.getY(i) - doorInst.getY(j)));
                if (denominator == 0) {
                    continue;
                }

                // multipliers to the equations of the lines. If they are lower than 0 or bigger than 1, we know that segments don't intersect
                final float multiplier1 = (float) (((doorInst.getX(j) - doorInst.getX(i)) * (y - doorInst.getY(i))) - ((doorInst.getY(j) - doorInst.getY(i)) * (x - doorInst.getX(i)))) / denominator;
                final float multiplier2 = (float) (((tx - x) * (y - doorInst.getY(i))) - ((ty - y) * (x - doorInst.getX(i)))) / denominator;
                if ((multiplier1 >= 0) && (multiplier1 <= 1) && (multiplier2 >= 0) && (multiplier2 <= 1)) {
                    final int intersectZ = Math.round(z + (multiplier1 * (tz - z)));
                    // now checking if the resulting point is between door's min and max z
                    if ((intersectZ > doorInst.getZMin()) && (intersectZ < doorInst.getZMax())) {
                        if (!doubleFaceCheck || intersectFace) {
                            return true;
                        }
                        intersectFace = true;
                    }
                }
            }
        }
        return false;
    }

    public static void init() {
        getInstance().load();
    }

    public static DoorDataManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final DoorDataManager INSTANCE = new DoorDataManager();
    }
}
