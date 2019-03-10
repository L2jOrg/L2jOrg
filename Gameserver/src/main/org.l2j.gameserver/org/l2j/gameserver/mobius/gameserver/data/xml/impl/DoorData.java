package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.commons.util.IXmlReader;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2DoorTemplate;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.mobius.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * This class loads and hold info about doors.
 *
 * @author JIV, GodKratos, UnAfraid
 */
public final class DoorData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(DoorData.class.getName());

    // Info holders
    private final Map<String, Set<Integer>> _groups = new HashMap<>();
    private final Map<Integer, L2DoorInstance> _doors = new HashMap<>();
    private final Map<Integer, StatsSet> _templates = new HashMap<>();
    private final Map<Integer, List<L2DoorInstance>> _regions = new HashMap<>();

    protected DoorData() {
        load();
    }

    public static DoorData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _doors.clear();
        _groups.clear();
        _regions.clear();
        parseDatapackFile("data/DoorData.xml");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "door", doorNode -> spawnDoor(parseDoor(doorNode))));
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _doors.size() + " Door Templates for " + _regions.size() + " regions.");
    }

    public StatsSet parseDoor(Node doorNode) {
        final StatsSet params = new StatsSet(parseAttributes(doorNode));
        params.set("baseHpMax", 1); // Avoid doors without HP value created dead due to default value 0 in L2CharTemplate

        forEach(doorNode, IXmlReader::isNode, innerDoorNode ->
        {
            final NamedNodeMap attrs = innerDoorNode.getAttributes();
            if (innerDoorNode.getNodeName().equals("nodes")) {
                params.set("nodeZ", parseInteger(attrs, "nodeZ"));

                final AtomicInteger count = new AtomicInteger();
                forEach(innerDoorNode, IXmlReader::isNode, nodes ->
                {
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

    /**
     * @param set
     */
    private void applyCollisions(StatsSet set) {
        // Insert Collision data
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

    /**
     * Spawns the door, adds the group name and registers it to templates, regions and doors also inserts collisions data
     *
     * @param set
     * @return
     */
    public L2DoorInstance spawnDoor(StatsSet set) {
        // Create door template + door instance
        final L2DoorTemplate template = new L2DoorTemplate(set);
        final L2DoorInstance door = spawnDoor(template, null);

        // Register the door
        _templates.put(door.getId(), set);
        _doors.put(door.getId(), door);
        _regions.computeIfAbsent(MapRegionManager.getInstance().getMapRegionLocId(door), key -> new ArrayList<>()).add(door);
        return door;
    }

    /**
     * Spawns the door, adds the group name and registers it to templates
     *
     * @param template
     * @param instance
     * @return a new door instance based on provided template
     */
    public L2DoorInstance spawnDoor(L2DoorTemplate template, Instance instance) {
        final L2DoorInstance door = new L2DoorInstance(template);
        door.setCurrentHp(door.getMaxHp());

        // Set instance world if provided
        if (instance != null) {
            door.setInstance(instance);
        }

        // Spawn the door on the world
        door.spawnMe(template.getX(), template.getY(), template.getZ());

        // Register door's group
        if (template.getGroupName() != null) {
            _groups.computeIfAbsent(door.getGroupName(), key -> new HashSet<>()).add(door.getId());
        }
        return door;
    }

    public StatsSet getDoorTemplate(int doorId) {
        return _templates.get(doorId);
    }

    public L2DoorInstance getDoor(int doorId) {
        return _doors.get(doorId);
    }

    public Set<Integer> getDoorsByGroup(String groupName) {
        return _groups.getOrDefault(groupName, Collections.emptySet());
    }

    public Collection<L2DoorInstance> getDoors() {
        return _doors.values();
    }

    public boolean checkIfDoorsBetween(Location start, Location end, Instance instance) {
        return checkIfDoorsBetween(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ(), instance);
    }

    public boolean checkIfDoorsBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance) {
        return checkIfDoorsBetween(x, y, z, tx, ty, tz, instance, false);
    }

    /**
     * GodKratos: TODO: remove GeoData checks from door table and convert door nodes to Geo zones
     *
     * @param x
     * @param y
     * @param z
     * @param tx
     * @param ty
     * @param tz
     * @param instance
     * @param doubleFaceCheck
     * @return {@code boolean}
     */
    public boolean checkIfDoorsBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance, boolean doubleFaceCheck) {
        final Collection<L2DoorInstance> allDoors = (instance != null) ? instance.getDoors() : _regions.get(MapRegionManager.getInstance().getMapRegionLocId(x, y));
        if (allDoors == null) {
            return false;
        }

        for (L2DoorInstance doorInst : allDoors) {
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

    private static class SingletonHolder {
        protected static final DoorData _instance = new DoorData();
    }
}
