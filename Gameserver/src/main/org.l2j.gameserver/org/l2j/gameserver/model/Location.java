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
package org.l2j.gameserver.model;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.interfaces.IPositionable;

/**
 * Location data transfer object.<br>
 * Contains coordinates data, heading and instance Id.
 *
 * @author Zoey76
 */
public class Location implements IPositionable {
    protected int _x;
    protected int _y;
    protected int _z;
    private int _heading;

    public Location(int x, int y, int z) {
        this(x, y, z, 0);
    }

    public Location(int x, int y, int z, int heading) {
        _x = x;
        _y = y;
        _z = z;
        _heading = heading;
    }

    public Location(WorldObject obj) {
        this(obj.getX(), obj.getY(), obj.getZ(), obj.getHeading());
    }

    public Location(StatsSet set) {
        _x = set.getInt("x");
        _y = set.getInt("y");
        _z = set.getInt("z");
        _heading = set.getInt("heading", 0);
    }

    /**
     * Get the x coordinate.
     *
     * @return the x coordinate
     */
    @Override
    public int getX() {
        return _x;
    }

    /**
     * Get the y coordinate.
     *
     * @return the y coordinate
     */
    @Override
    public int getY() {
        return _y;
    }

    /**
     * Get the z coordinate.
     *
     * @return the z coordinate
     */
    @Override
    public int getZ() {
        return _z;
    }

    /**
     * Set the x, y, z coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    @Override
    public void setXYZ(int x, int y, int z) {
        _x = x;
        _y = y;
        _z = z;
    }

    /**
     * Set the x, y, z coordinates.
     *
     * @param loc The location.
     */
    @Override
    public void setXYZ(ILocational loc) {
        setXYZ(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Get the heading.
     *
     * @return the heading
     */
    @Override
    public int getHeading() {
        return _heading;
    }

    /**
     * Set the heading.
     *
     * @param heading the heading
     */
    @Override
    public void setHeading(int heading) {
        _heading = heading;
    }

    @Override
    public IPositionable getLocation() {
        return this;
    }

    @Override
    public void setLocation(Location loc) {
        _x = loc.getX();
        _y = loc.getY();
        _z = loc.getZ();
        _heading = loc.getHeading();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location loc) {
            return (getX() == loc.getX()) && (getY() == loc.getY()) && (getZ() == loc.getZ()) && (getHeading() == loc.getHeading());
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] X: " + _x + " Y: " + _y + " Z: " + _z + " Heading: " + _heading;
    }
}
