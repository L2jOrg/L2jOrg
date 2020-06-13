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

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Fence;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author HoridoJoho / FBIagent
 */
public final class FenceDataManager extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FenceDataManager.class);

    private static final int MAX_Z_DIFF = 100;

    private final Map<WorldRegion, List<Fence>> regions = new ConcurrentHashMap<>();
    private final IntMap<Fence> fences = new CHashIntMap<>();

    private FenceDataManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/FenceData.xsd");
    }

    @Override
    public void load() {
        if (!fences.isEmpty()) {
            fences.values().forEach(this::removeFence);
        }

        parseDatapackFile("data/FenceData.xml");
        LOGGER.info("Loaded {} Fences", fences.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "fence", this::spawnFence));
    }

    private void spawnFence(Node fenceNode) {
        final StatsSet set = new StatsSet(parseAttributes(fenceNode));
        spawnFence(set.getInt("x"), set.getInt("y"), set.getInt("z"), set.getString("name"), set.getInt("width"), set.getInt("length"), set.getInt("height"), 0, set.getEnum("state", FenceState.class, FenceState.CLOSED));
    }

    public Fence spawnFence(int x, int y, int z, int width, int length, int height, int instanceId, FenceState state) {
        return spawnFence(x, y, z, null, width, length, height, instanceId, state);
    }

    private Fence spawnFence(int x, int y, int z, String name, int width, int length, int height, int instanceId, FenceState state) {
        final Fence fence = new Fence(x, y, name, width, length, height, state);
        if (instanceId > 0) {
            fence.setInstanceById(instanceId);
        }
        fence.spawnMe(x, y, z);
        addFence(fence);

        return fence;
    }

    private void addFence(Fence fence) {
        fences.put(fence.getObjectId(), fence);
        regions.computeIfAbsent(World.getInstance().getRegion(fence), key -> new ArrayList<>()).add(fence);
    }

    public void removeFence(Fence fence) {
        fences.remove(fence.getObjectId());

        final List<Fence> fencesInRegion = regions.get(World.getInstance().getRegion(fence));
        if (fencesInRegion != null) {
            fencesInRegion.remove(fence);
        }
    }

    public IntMap<Fence> getFences() {
        return fences;
    }

    public boolean checkIfFenceBetween(int x, int y, int z, int tx, int ty, int tz, Instance instance) {
        final Predicate<Fence> filter = fence ->
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
                return (z > (fence.getZ() - MAX_Z_DIFF)) && (z < (fence.getZ() + MAX_Z_DIFF));
            }

            return false;
        };

        final WorldRegion region = World.getInstance().getRegion(x, y); // Should never be null.
        return region != null && regions.getOrDefault(region, Collections.emptyList()).stream().anyMatch(filter);
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
        return (yCross <= yMax) && (yCross >= yMin);
    }

    private double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        final double d = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4));
        if (d == 0) {
            return null;
        }

        final double xi = (((x3 - x4) * ((x1 * y2) - (y1 * x2))) - ((x1 - x2) * ((x3 * y4) - (y3 * x4)))) / d;
        final double yi = (((y3 - y4) * ((x1 * y2) - (y1 * x2))) - ((y1 - y2) * ((x3 * y4) - (y3 * x4)))) / d;

        return new double[]  { xi, yi };
    }

    public static void init() {
        getInstance().load();
    }

    public static FenceDataManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final FenceDataManager INSTANCE = new FenceDataManager();
    }
}