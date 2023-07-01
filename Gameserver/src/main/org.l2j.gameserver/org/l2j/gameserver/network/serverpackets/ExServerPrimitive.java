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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import javax.swing.*;
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

    private int _size = 0;
    private ExServerPrimitive _next;

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

    public void addLine(String name, int color, boolean isNameColored, int x, int y, int x2, int y2, int z2) {
        _lines.add(new Line(name, color, isNameColored, x, y, x2, y2, z2));
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

    public void addLine(String name, Color color, boolean isNameColored, int x, int y, int x2, int y2, int z2) {
        addLine(name, color.getRGB(), isNameColored, x, y, x2, y2, z2);
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

    public void addLine(Color color, int x, int y, int x2, int y2, int z2) {
        addLine("", color, false, x, y, x2, y2, z2);
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
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SERVER_PRIMITIVE, buffer );

        buffer.writeString(_name);
        buffer.writeInt(_x);
        buffer.writeInt(_y);
        buffer.writeInt(_z);
        buffer.writeInt(65535); // has to do something with display range and angle
        buffer.writeInt(65535); // has to do something with display range and angle

        buffer.writeInt(_points.size() + _lines.size());

        for (Point point : _points) {
            buffer.writeByte(1); // Its the type in this case Point
            writePoint(point, buffer);
        }

        for (Line line : _lines) {
            buffer.writeByte(2); // Its the type in this case Line
            writePoint(line, buffer);
            buffer.writeInt(line.getX2());
            buffer.writeInt(line.getY2());
            buffer.writeInt(line.getZ2());
        }
    }

    private void writePoint(Point line, WritableBuffer buffer) {
        buffer.writeString(line.getName());
        final int color = line.getColor();
        buffer.writeInt((color >> 16) & 0xFF); // R
        buffer.writeInt((color >> 8) & 0xFF); // G
        buffer.writeInt(color & 0xFF); // B
        buffer.writeInt(line.isNameColored() ? 1 : 0);
        buffer.writeInt(line.getX());
        buffer.writeInt(line.getY());
        buffer.writeInt(line.getZ());
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

        public Line(String name, int color, boolean isNameColored, int x, int y, int x2, int y2, int z2) {
            super(name, color, isNameColored, x, y, z2);
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

    /* Autoplay testing area:
     * creates a circle around a point
     * @author Bru7aLMike
     */
    public static ExServerPrimitive createCirclePacket(String name, int x, int y, int z, int radius, Color color, int initX, int initY)
    {
        ExServerPrimitive packet = new ExServerPrimitive(name, initX, initY, z + 50);
        int i = 0;

        for(short MaxDegree = 359; i <= MaxDegree; ++i) {
            double var1 = Math.toRadians(i);
            double var2 = Math.toRadians((double)i + (double)1);
            int newX = (int)(x + radius * Math.cos(var1));
            int newY = (int)(y + radius * Math.sin(var1));
            int newXT = (int)(x + radius * Math.cos(var2));
            int newYT = (int)(y + radius * Math.sin(var2));
            Location loc = new Location(newX, newY, z);
            Location locPlus = new Location(newXT, newYT, z);
            packet.addLine(color, loc, locPlus);
        }

        return packet;
    }

    public static void clearCircle(Player player, String circleName) {
        ExServerPrimitive packet = new ExServerPrimitive(circleName, player);
        packet.addPoint(Color.WHITE, 0, 0, 0);
        player.sendPacket(packet);
    }

    /**
     * Reset both lines and points {@link List}s.
     */
    public void reset()
    {
        _lines.clear();
        _points.clear();
        _size = 0;

        if (_next != null)
            _next.reset();
    }

    /**
     * Send packet to the {@link Player}. If out of capacity, send more packets.
     * @param player : The {@link Player} to send packet(s) to.
     */
    public void sendTo(Player player)
    {
        // Packet is empty, add dummy points (happens at first packet only).
        if (_size == 0)
            addPoint(Color.WHITE, _x, _y, 16384);

        // Send packet.
        player.sendPacket(this);

        // No next packet, return.
        if (_next == null)
            return;

        // Check next packet.
        if (_next._size == 0)
        {
            // Next packet is empty, add dummy point.
            _next.addPoint(Color.WHITE, _x, _y, 16384);

            // Send packet and remove next packet.
            _next.sendTo(player);
            _next = null;
        }
        else
            // Next packet is not empty, send packet.
            _next.sendTo(player);
    }
}