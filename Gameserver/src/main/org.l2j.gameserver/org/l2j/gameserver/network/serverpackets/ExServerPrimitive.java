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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A packet used to draw points and lines on client.<br/>
 * <b>Note:</b> Names in points and lines are bugged they will appear even when not looking at them.
 *
 * @author NosBit
 */
public class ExServerPrimitive extends ServerPacket {
    private final String _name;
    private final int _x;
    private final int _y;
    private final int _z;
    private final List<Point> _points = new ArrayList<>();
    private final List<Line> _lines = new ArrayList<>();

    /**
     * @param name A unique name this will be used to replace lines if second packet is sent
     * @param x    the x coordinate usually middle of drawing area
     * @param y    the y coordinate usually middle of drawing area
     * @param z    the z coordinate usually middle of drawing area
     */
    public ExServerPrimitive(String name, int x, int y, int z) {
        _name = name;
        _x = x;
        _y = y;
        _z = z;
    }

    /**
     * @param name       A unique name this will be used to replace lines if second packet is sent
     * @param locational the ILocational to take coordinates usually middle of drawing area
     */
    public ExServerPrimitive(String name, ILocational locational) {
        this(name, locational.getX(), locational.getY(), locational.getZ());
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param name          the name that will be displayed over the point
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param x             the x coordinate for this point
     * @param y             the y coordinate for this point
     * @param z             the z coordinate for this point
     */
    public void addPoint(String name, int color, boolean isNameColored, int x, int y, int z) {
        _points.add(new Point(name, color, isNameColored, x, y, z));
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param name          the name that will be displayed over the point
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param locational    the ILocational to take coordinates for this point
     */
    public void addPoint(String name, int color, boolean isNameColored, ILocational locational) {
        addPoint(name, color, isNameColored, locational.getX(), locational.getY(), locational.getZ());
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param color the color
     * @param x     the x coordinate for this point
     * @param y     the y coordinate for this point
     * @param z     the z coordinate for this point
     */
    public void addPoint(int color, int x, int y, int z) {
        addPoint("", color, false, x, y, z);
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param color      the color
     * @param locational the ILocational to take coordinates for this point
     */
    public void addPoint(int color, ILocational locational) {
        addPoint("", color, false, locational);
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param name          the name that will be displayed over the point
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param x             the x coordinate for this point
     * @param y             the y coordinate for this point
     * @param z             the z coordinate for this point
     */
    public void addPoint(String name, Color color, boolean isNameColored, int x, int y, int z) {
        addPoint(name, color.getRGB(), isNameColored, x, y, z);
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param name          the name that will be displayed over the point
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param locational    the ILocational to take coordinates for this point
     */
    public void addPoint(String name, Color color, boolean isNameColored, ILocational locational) {
        addPoint(name, color.getRGB(), isNameColored, locational);
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param color the color
     * @param x     the x coordinate for this point
     * @param y     the y coordinate for this point
     * @param z     the z coordinate for this point
     */
    public void addPoint(Color color, int x, int y, int z) {
        addPoint("", color, false, x, y, z);
    }

    /**
     * Adds a point to be displayed on client.
     *
     * @param color      the color
     * @param locational the ILocational to take coordinates for this point
     */
    public void addPoint(Color color, ILocational locational) {
        addPoint("", color, false, locational);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param x             the x coordinate for this line start point
     * @param y             the y coordinate for this line start point
     * @param z             the z coordinate for this line start point
     * @param x2            the x coordinate for this line end point
     * @param y2            the y coordinate for this line end point
     * @param z2            the z coordinate for this line end point
     */
    public void addLine(String name, int color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2) {
        _lines.add(new Line(name, color, isNameColored, x, y, z, x2, y2, z2));
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param locational    the ILocational to take coordinates for this line start point
     * @param x2            the x coordinate for this line end point
     * @param y2            the y coordinate for this line end point
     * @param z2            the z coordinate for this line end point
     */
    public void addLine(String name, int color, boolean isNameColored, ILocational locational, int x2, int y2, int z2) {
        addLine(name, color, isNameColored, locational.getX(), locational.getY(), locational.getZ(), x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param x             the x coordinate for this line start point
     * @param y             the y coordinate for this line start point
     * @param z             the z coordinate for this line start point
     * @param locational2   the ILocational to take coordinates for this line end point
     */
    public void addLine(String name, int color, boolean isNameColored, int x, int y, int z, ILocational locational2) {
        addLine(name, color, isNameColored, x, y, z, locational2.getX(), locational2.getY(), locational2.getZ());
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param locational    the ILocational to take coordinates for this line start point
     * @param locational2   the ILocational to take coordinates for this line end point
     */
    public void addLine(String name, int color, boolean isNameColored, ILocational locational, ILocational locational2) {
        addLine(name, color, isNameColored, locational, locational2.getX(), locational2.getY(), locational2.getZ());
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color the color
     * @param x     the x coordinate for this line start point
     * @param y     the y coordinate for this line start point
     * @param z     the z coordinate for this line start point
     * @param x2    the x coordinate for this line end point
     * @param y2    the y coordinate for this line end point
     * @param z2    the z coordinate for this line end point
     */
    public void addLine(int color, int x, int y, int z, int x2, int y2, int z2) {
        addLine("", color, false, x, y, z, x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color      the color
     * @param locational the ILocational to take coordinates for this line start point
     * @param x2         the x coordinate for this line end point
     * @param y2         the y coordinate for this line end point
     * @param z2         the z coordinate for this line end point
     */
    public void addLine(int color, ILocational locational, int x2, int y2, int z2) {
        addLine("", color, false, locational, x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color       the color
     * @param x           the x coordinate for this line start point
     * @param y           the y coordinate for this line start point
     * @param z           the z coordinate for this line start point
     * @param locational2 the ILocational to take coordinates for this line end point
     */
    public void addLine(int color, int x, int y, int z, ILocational locational2) {
        addLine("", color, false, x, y, z, locational2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color       the color
     * @param locational  the ILocational to take coordinates for this line start point
     * @param locational2 the ILocational to take coordinates for this line end point
     */
    public void addLine(int color, ILocational locational, ILocational locational2) {
        addLine("", color, false, locational, locational2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param x             the x coordinate for this line start point
     * @param y             the y coordinate for this line start point
     * @param z             the z coordinate for this line start point
     * @param x2            the x coordinate for this line end point
     * @param y2            the y coordinate for this line end point
     * @param z2            the z coordinate for this line end point
     */
    public void addLine(String name, Color color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2) {
        addLine(name, color.getRGB(), isNameColored, x, y, z, x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param locational    the ILocational to take coordinates for this line start point
     * @param x2            the x coordinate for this line end point
     * @param y2            the y coordinate for this line end point
     * @param z2            the z coordinate for this line end point
     */
    public void addLine(String name, Color color, boolean isNameColored, ILocational locational, int x2, int y2, int z2) {
        addLine(name, color.getRGB(), isNameColored, locational, x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param x             the x coordinate for this line start point
     * @param y             the y coordinate for this line start point
     * @param z             the z coordinate for this line start point
     * @param locational2   the ILocational to take coordinates for this line end point
     */
    public void addLine(String name, Color color, boolean isNameColored, int x, int y, int z, ILocational locational2) {
        addLine(name, color.getRGB(), isNameColored, x, y, z, locational2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param name          the name that will be displayed over the middle of line
     * @param color         the color
     * @param isNameColored if {@code true} name will be colored as well.
     * @param locational    the ILocational to take coordinates for this line start point
     * @param locational2   the ILocational to take coordinates for this line end point
     */
    public void addLine(String name, Color color, boolean isNameColored, ILocational locational, ILocational locational2) {
        addLine(name, color.getRGB(), isNameColored, locational, locational2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color the color
     * @param x     the x coordinate for this line start point
     * @param y     the y coordinate for this line start point
     * @param z     the z coordinate for this line start point
     * @param x2    the x coordinate for this line end point
     * @param y2    the y coordinate for this line end point
     * @param z2    the z coordinate for this line end point
     */
    public void addLine(Color color, int x, int y, int z, int x2, int y2, int z2) {
        addLine("", color, false, x, y, z, x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color      the color
     * @param locational the ILocational to take coordinates for this line start point
     * @param x2         the x coordinate for this line end point
     * @param y2         the y coordinate for this line end point
     * @param z2         the z coordinate for this line end point
     */
    public void addLine(Color color, ILocational locational, int x2, int y2, int z2) {
        addLine("", color, false, locational, x2, y2, z2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color       the color
     * @param x           the x coordinate for this line start point
     * @param y           the y coordinate for this line start point
     * @param z           the z coordinate for this line start point
     * @param locational2 the ILocational to take coordinates for this line end point
     */
    public void addLine(Color color, int x, int y, int z, ILocational locational2) {
        addLine("", color, false, x, y, z, locational2);
    }

    /**
     * Adds a line to be displayed on client
     *
     * @param color       the color
     * @param locational  the ILocational to take coordinates for this line start point
     * @param locational2 the ILocational to take coordinates for this line end point
     */
    public void addLine(Color color, ILocational locational, ILocational locational2) {
        addLine("", color, false, locational, locational2);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SERVER_PRIMITIVE);

        writeString(_name);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(65535); // has to do something with display range and angle
        writeInt(65535); // has to do something with display range and angle

        writeInt(_points.size() + _lines.size());

        for (Point point : _points) {
            writeByte((byte) 1); // Its the type in this case Point
            writePoint(point);
        }

        for (Line line : _lines) {
            writeByte((byte) 2); // Its the type in this case Line
            writePoint(line);
            writeInt(line.getX2());
            writeInt(line.getY2());
            writeInt(line.getZ2());
        }
    }

    private void writePoint(Point line) {
        writeString(line.getName());
        final int color = line.getColor();
        writeInt((color >> 16) & 0xFF); // R
        writeInt((color >> 8) & 0xFF); // G
        writeInt(color & 0xFF); // B
        writeInt(line.isNameColored() ? 1 : 0);
        writeInt(line.getX());
        writeInt(line.getY());
        writeInt(line.getZ());
    }


    private static class Point {
        private final String _name;
        private final int _color;
        private final boolean _isNameColored;
        private final int _x;
        private final int _y;
        private final int _z;

        public Point(String name, int color, boolean isNameColored, int x, int y, int z) {
            _name = name;
            _color = color;
            _isNameColored = isNameColored;
            _x = x;
            _y = y;
            _z = z;
        }

        /**
         * @return the name
         */
        public String getName() {
            return _name;
        }

        /**
         * @return the color
         */
        public int getColor() {
            return _color;
        }

        /**
         * @return the isNameColored
         */
        public boolean isNameColored() {
            return _isNameColored;
        }

        /**
         * @return the x
         */
        public int getX() {
            return _x;
        }

        /**
         * @return the y
         */
        public int getY() {
            return _y;
        }

        /**
         * @return the z
         */
        public int getZ() {
            return _z;
        }
    }

    private static class Line extends Point {
        private final int _x2;
        private final int _y2;
        private final int _z2;

        public Line(String name, int color, boolean isNameColored, int x, int y, int z, int x2, int y2, int z2) {
            super(name, color, isNameColored, x, y, z);
            _x2 = x2;
            _y2 = y2;
            _z2 = z2;
        }

        /**
         * @return the x2
         */
        public int getX2() {
            return _x2;
        }

        /**
         * @return the y2
         */
        public int getY2() {
            return _y2;
        }

        /**
         * @return the z2
         */
        public int getZ2() {
            return _z2;
        }
    }
}