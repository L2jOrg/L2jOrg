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
package org.l2j.gameserver.engine.geo.pathfinding;

import org.l2j.gameserver.engine.geo.geodata.GeoStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DS, Hasha; Credits to Diamond
 * @author JoeAlisson
 */
public class NodeBuffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeBuffer.class);

    private static final int BASE_WEIGHT = 10;
    private static final int DIAGONAL_WEIGHT = 14;
    private static final int HEURISTIC_WEIGHT = 20;
    private static final int OBSTACLE_MULTIPLIER = 10;
    private static final int MAX_ITERATIONS = 3500;

    private final ReentrantLock _lock = new ReentrantLock();
    private final int _size;
    private final Node[][] _buffer;

    // center coordinates
    private int _cx = 0;
    private int _cy = 0;

    // target coordinates
    private int _gtx = 0;
    private int _gty = 0;
    private short _gtz = 0;

    private Node _current = null;

    /**
     * Constructor of NodeBuffer.
     *
     * @param size : one dimension size of buffer
     */
    public NodeBuffer(int size) {
        // set size
        _size = size;

        // initialize buffer
        _buffer = new Node[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                _buffer[x][y] = new Node();
            }
        }
    }

    /**
     * Find path consisting of Nodes. Starts at origin coordinates, ends in target coordinates.
     *
     * @param gox : origin point x
     * @param goy : origin point y
     * @param goz : origin point z
     * @param gtx : target point x
     * @param gty : target point y
     * @param gtz : target point z
     * @return Node : first node of path
     */
    public final Node findPath(int gox, int goy, short goz, int gtx, int gty, short gtz) {

        // set coordinates (middle of the line (gox,goy) - (gtx,gty), will be in the center of the buffer)
        _cx = gox + ((gtx - gox - _size) / 2);
        _cy = goy + ((gty - goy - _size) / 2);

        _gtx = gtx;
        _gty = gty;
        _gtz = gtz;

        _current = getNode(gox, goy, goz);
        _current.setCost(getCostH(gox, goy, goz));

        int count = 0;
        do {
            // reached target?
            if ((_current.getLoc().getGeoX() == _gtx) && (_current.getLoc().getGeoY() == _gty) && (Math.abs(_current.getLoc().getZ() - _gtz) < 8)) {
                return _current;
            }

            // expand current node
            expand();

            // move pointer
            _current = _current.getChild();
        }
        while ((_current != null) && (++count < MAX_ITERATIONS));

        return null;
    }

    public final boolean isLocked() {
        return _lock.tryLock();
    }

    public final void free() {
        _current = null;

        for (Node[] nodes : _buffer) {
            for (Node node : nodes) {
                if (node.getLoc() != null) {
                    node.free();
                }
            }
        }

        _lock.unlock();
    }

    /**
     * Check _current Node and add its neighbors to the buffer.
     */
    private final void expand() {
        // can't move anywhere, don't expand
        byte nswe = _current.getLoc().getNSWE();
        if (nswe == 0) {
            return;
        }

        // get geo coords of the node to be expanded
        final int x = _current.getLoc().getGeoX();
        final int y = _current.getLoc().getGeoY();
        final short z = (short) _current.getLoc().getZ();

        // can move north, expand
        if ((nswe & GeoStructure.CELL_FLAG_N) != 0) {
            addNode(x, y - 1, z, BASE_WEIGHT);
        }

        // can move south, expand
        if ((nswe & GeoStructure.CELL_FLAG_S) != 0) {
            addNode(x, y + 1, z, BASE_WEIGHT);
        }

        // can move west, expand
        if ((nswe & GeoStructure.CELL_FLAG_W) != 0) {
            addNode(x - 1, y, z, BASE_WEIGHT);
        }

        // can move east, expand
        if ((nswe & GeoStructure.CELL_FLAG_E) != 0) {
            addNode(x + 1, y, z, BASE_WEIGHT);
        }

        // can move north-west, expand
        if ((nswe & GeoStructure.CELL_FLAG_NW) != 0) {
            addNode(x - 1, y - 1, z, DIAGONAL_WEIGHT);
        }

        // can move north-east, expand
        if ((nswe & GeoStructure.CELL_FLAG_NE) != 0) {
            addNode(x + 1, y - 1, z, DIAGONAL_WEIGHT);
        }

        // can move south-west, expand
        if ((nswe & GeoStructure.CELL_FLAG_SW) != 0) {
            addNode(x - 1, y + 1, z, DIAGONAL_WEIGHT);
        }

        // can move south-east, expand
        if ((nswe & GeoStructure.CELL_FLAG_SE) != 0) {
            addNode(x + 1, y + 1, z, DIAGONAL_WEIGHT);
        }
    }

    /**
     * Returns node, if it exists in buffer.
     *
     * @param x : node X coord
     * @param y : node Y coord
     * @param z : node Z coord
     * @return Node : node, if exits in buffer
     */
    private final Node getNode(int x, int y, short z) {
        // check node X out of coordinates
        final int ix = x - _cx;
        if ((ix < 0) || (ix >= _size)) {
            return null;
        }

        // check node Y out of coordinates
        final int iy = y - _cy;
        if ((iy < 0) || (iy >= _size)) {
            return null;
        }

        // get node
        Node result = _buffer[ix][iy];

        // check and update
        if (result.getLoc() == null) {
            result.setLoc(x, y, z);
        }

        // return node
        return result;
    }

    /**
     * Add node given by coordinates to the buffer.
     *
     * @param x      : geo X coord
     * @param y      : geo Y coord
     * @param z      : geo Z coord
     * @param weight : weight of movement to new node
     */
    private final void addNode(int x, int y, short z, int weight) {
        // get node to be expanded
        Node node = getNode(x, y, z);
        if (node == null) {
            return;
        }

        // Z distance between nearby cells is higher than cell size
        if (node.getLoc().getZ() > (z + (2 * GeoStructure.CELL_HEIGHT))) {
            return;
        }

        // node was already expanded, return
        if (node.getCost() >= 0) {
            return;
        }

        node.setParent(_current);
        if (node.getLoc().getNSWE() != (byte) 0xFF) {
            node.setCost(getCostH(x, y, node.getLoc().getZ()) + (weight * OBSTACLE_MULTIPLIER));
        } else {
            node.setCost(getCostH(x, y, node.getLoc().getZ()) + weight);
        }

        Node current = _current;
        int count = 0;
        while ((current.getChild() != null) && (count < (MAX_ITERATIONS * 4))) {
            count++;
            if (current.getChild().getCost() > node.getCost()) {
                node.setChild(current.getChild());
                break;
            }
            current = current.getChild();
        }

        if (count >= (MAX_ITERATIONS * 4)) {
            LOGGER.warn("Too long loop detected, cost: {}", node.getCost());
        }

        current.setChild(node);
    }

    /**
     * @param x : node X coord
     * @param y : node Y coord
     * @param i : node Z coord
     * @return double : node cost
     */
    private final double getCostH(int x, int y, int i) {
        final int dX = x - _gtx;
        final int dY = y - _gty;
        final int dZ = (i - _gtz) / GeoStructure.CELL_HEIGHT;

        return Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ)) * HEURISTIC_WEIGHT; // Direct distance
    }
}