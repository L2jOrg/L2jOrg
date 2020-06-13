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
package org.l2j.gameserver.engine.geo;

import org.l2j.gameserver.engine.geo.geodata.GeoLocation;
import org.l2j.gameserver.engine.geo.pathfinding.Node;
import org.l2j.gameserver.engine.geo.pathfinding.NodeBuffer;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.instancezone.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Hasha
 * @author JoeAlisson
 */
final class GeoEnginePathFinding extends GeoEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoEnginePathFinding.class);

    private final BufferHolder[] buffers = {
        new BufferHolder(100, 6),
        new BufferHolder(128, 6),
        new BufferHolder(192, 6),
        new BufferHolder(256, 4),
        new BufferHolder(320, 4),
        new BufferHolder(384, 4),
        new BufferHolder(500, 2)
    };

    GeoEnginePathFinding() {
        LOGGER.info("Loaded {} path node buffers.", buffers.length);
    }

    /**
     * Create list of node locations as result of calculated buffer node tree.
     *
     * @param target : the entry point
     * @return List<NodeLoc> : list of node location
     */
    private static List<Location> constructPath(Node target) {
        // create empty list
        LinkedList<Location> list = new LinkedList<>();

        // set direction X/Y
        int dx = 0;
        int dy = 0;

        // get target parent
        Node parent = target.getParent();

        // while parent exists
        while (parent != null) {
            // get parent <> target direction X/Y
            final int nx = parent.getLoc().getGeoX() - target.getLoc().getGeoX();
            final int ny = parent.getLoc().getGeoY() - target.getLoc().getGeoY();

            // direction has changed?
            if ((dx != nx) || (dy != ny)) {
                // add node to the beginning of the list
                list.addFirst(target.getLoc());

                // update direction X/Y
                dx = nx;
                dy = ny;
            }

            // move to next node, set target and get its parent
            target = parent;
            parent = target.getParent();
        }

        // return list
        return list;
    }

    @Override
    public List<Location> findPath(int ox, int oy, int oz, int tx, int ty, int tz, Instance instance) {
        // get origin and check existing geo coords
        int gox = getGeoX(ox);
        int goy = getGeoY(oy);
        if (!hasGeoPos(gox, goy)) {
            return null;
        }

        short goz = getHeightNearest(gox, goy, oz);

        // get target and check existing geo coords
        int gtx = getGeoX(tx);
        int gty = getGeoY(ty);
        if (!hasGeoPos(gtx, gty)) {
            return null;
        }

        short gtz = getHeightNearest(gtx, gty, tz);

        // Prepare buffer for pathfinding calculations
        final NodeBuffer buffer = getBuffer(64 + (2 * Math.max(Math.abs(gox - gtx), Math.abs(goy - gty))));
        if (buffer == null) {
            return null;
        }

        // find path
        List<Location> path = null;
        try {
            Node result = buffer.findPath(gox, goy, goz, gtx, gty, gtz);

            if (result == null) {
                return null;
            }

            path = constructPath(result);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return null;
        } finally {
            buffer.free();
        }

        // check path
        if (path.size() < 3) {
            return path;
        }

        // get path list iterator
        ListIterator<Location> point = path.listIterator();

        // get node A (origin)
        int nodeAx = gox;
        int nodeAy = goy;
        short nodeAz = goz;

        // get node B
        GeoLocation nodeB = (GeoLocation) point.next();

        // iterate thought the path to optimize it
        while (point.hasNext()) {
            // get node C
            GeoLocation nodeC = (GeoLocation) path.get(point.nextIndex());

            // check movement from node A to node C
            GeoLocation loc = checkMove(nodeAx, nodeAy, nodeAz, nodeC.getGeoX(), nodeC.getGeoY(), nodeC.getZ(), instance);
            if ((loc.getGeoX() == nodeC.getGeoX()) && (loc.getGeoY() == nodeC.getGeoY())) {
                // can move from node A to node C

                // remove node B
                point.remove();
            } else {
                // can not move from node A to node C

                // set node A (node B is part of path, update A coordinates)
                nodeAx = nodeB.getGeoX();
                nodeAy = nodeB.getGeoY();
                nodeAz = (short) nodeB.getZ();
            }

            // set node B
            nodeB = (GeoLocation) point.next();
        }

        return path;
    }

    /**
     * Provides optimize selection of the buffer. When all pre-initialized buffer are locked, creates new buffer.
     *
     * @param size : pre-calculated minimal required size
     * @return NodeBuffer : buffer
     */
    private final NodeBuffer getBuffer(int size) {
        NodeBuffer current = null;
        for (BufferHolder holder : buffers) {
            // Find proper size of buffer
            if (holder._size < size) {
                continue;
            }

            // Find unlocked NodeBuffer
            for (NodeBuffer buffer : holder._buffer) {
                if (!buffer.isLocked()) {
                    continue;
                }

                return buffer;
            }

            // NodeBuffer not found, allocate temporary buffer
            current = new NodeBuffer(holder._size);
            current.isLocked();
        }

        return current;
    }

    /**
     * NodeBuffer container with specified size and count of separate buffers.
     */
    private static final class BufferHolder {
        final int _size;
        List<NodeBuffer> _buffer;

        public BufferHolder(int size, int count) {
            _size = size;
            _buffer = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                _buffer.add(new NodeBuffer(size));
            }
        }
    }
}