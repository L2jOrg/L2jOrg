package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.L2WorldRegion;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.L2FenceInstance;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author HoridoJoho / FBIagent
 */
public final class FenceData implements IGameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenceData.class);

    private static final int MAX_Z_DIFF = 100;

    private final Map<L2WorldRegion, List<L2FenceInstance>> _regions = new ConcurrentHashMap<>();
    private final Map<Integer, L2FenceInstance> _fences = new ConcurrentHashMap<>();

    private FenceData() {
        load();
    }

    @Override
    public void load() {
        if (!_fences.isEmpty()) {
            _fences.values().forEach(this::removeFence);
        }

        parseDatapackFile("data/FenceData.xml");
        LOGGER.info("Loaded {} Fences", _fences.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "fence", this::spawnFence));
    }

    public int getLoadedElementsCount() {
        return _fences.size();
    }

    private void spawnFence(Node fenceNode) {
        final StatsSet set = new StatsSet(parseAttributes(fenceNode));
        spawnFence(set.getInt("x"), set.getInt("y"), set.getInt("z"), set.getString("name"), set.getInt("width"), set.getInt("length"), set.getInt("height"), 0, set.getEnum("state", FenceState.class, FenceState.CLOSED));
    }

    public L2FenceInstance spawnFence(int x, int y, int z, int width, int length, int height, int instanceId, FenceState state) {
        return spawnFence(x, y, z, null, width, length, height, instanceId, state);
    }

    public L2FenceInstance spawnFence(int x, int y, int z, String name, int width, int length, int height, int instanceId, FenceState state) {
        final L2FenceInstance fence = new L2FenceInstance(x, y, name, width, length, height, state);
        if (instanceId > 0) {
            fence.setInstanceById(instanceId);
        }
        fence.spawnMe(x, y, z);
        addFence(fence);

        return fence;
    }

    private void addFence(L2FenceInstance fence) {
        _fences.put(fence.getObjectId(), fence);
        _regions.computeIfAbsent(L2World.getInstance().getRegion(fence), key -> new ArrayList<>()).add(fence);
    }

    public void removeFence(L2FenceInstance fence) {
        _fences.remove(fence.getObjectId());

        final List<L2FenceInstance> fencesInRegion = _regions.get(L2World.getInstance().getRegion(fence));
        if (fencesInRegion != null) {
            fencesInRegion.remove(fence);
        }
    }

    public Map<Integer, L2FenceInstance> getFences() {
        return _fences;
    }

    public L2FenceInstance getFence(int objectId) {
        return _fences.get(objectId);
    }

    public boolean checkIfFenceBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance) {
        final Predicate<L2FenceInstance> filter = fence ->
        {
            // Check if fence is geodata enabled.
            if (!fence.getState().isGeodataEnabled()) {
                return false;
            }

            // Check if fence is within the instance we search for.
            final int instanceId = (instance == null) ? 0 : instance.getId();
            if (fence.getInstanceId() != instanceId) {
                return false;
            }

            final int xMin = fence.getXMin();
            final int xMax = fence.getXMax();
            final int yMin = fence.getYMin();
            final int yMax = fence.getYMax();
            if ((x < xMin) && (tx < xMin)) {
                return false;
            }
            if ((x > xMax) && (tx > xMax)) {
                return false;
            }
            if ((y < yMin) && (ty < yMin)) {
                return false;
            }
            if ((y > yMax) && (ty > yMax)) {
                return false;
            }
            if ((x > xMin) && (tx > xMin) && (x < xMax) && (tx < xMax)) {
                if ((y > yMin) && (ty > yMin) && (y < yMax) && (ty < yMax)) {
                    return false;
                }
            }

            if (crossLinePart(xMin, yMin, xMax, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMax, yMin, xMax, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMax, yMax, xMin, yMax, x, y, tx, ty, xMin, yMin, xMax, yMax) || crossLinePart(xMin, yMax, xMin, yMin, x, y, tx, ty, xMin, yMin, xMax, yMax)) {
                if ((z > (fence.getZ() - MAX_Z_DIFF)) && (z < (fence.getZ() + MAX_Z_DIFF))) {
                    return true;
                }
            }

            return false;
        };

        final L2WorldRegion region = L2World.getInstance().getRegion(x, y); // Should never be null.
        return region != null && _regions.getOrDefault(region, Collections.emptyList()).stream().anyMatch(filter);
    }

    private boolean crossLinePart(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double xMin, double yMin, double xMax, double yMax) {
        final double[] result = intersection(x1, y1, x2, y2, x3, y3, x4, y4);
        if (result == null) {
            return false;
        }

        final double xCross = result[0];
        final double yCross = result[1];
        if ((xCross <= xMax) && (xCross >= xMin)) {
            return true;
        }
        if ((yCross <= yMax) && (yCross >= yMin)) {
            return true;
        }

        return false;
    }

    private double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        final double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (d == 0) {
            return null;
        }

        final double xi = (((x3 - x4) * ((x1 * y2) - (y1 * x2))) - ((x1 - x2) * ((x3 * y4) - (y3 * x4)))) / d;
        final double yi = (((y3 - y4) * ((x1 * y2) - (y1 * x2))) - ((y1 - y2) * ((x3 * y4) - (y3 * x4)))) / d;

        return new double[]
                {
                        xi,
                        yi
                };
    }

    public static FenceData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final FenceData INSTANCE = new FenceData();
    }
}