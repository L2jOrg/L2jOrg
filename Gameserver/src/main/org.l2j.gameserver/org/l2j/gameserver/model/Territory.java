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

import org.l2j.commons.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Balancer
 * @version 0.1, 2005-03-12
 */
public class Territory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Territory.class);
    private final List<Point> _points = Collections.synchronizedList(new ArrayList<>());
    private final int _terr;
    private int _xMin;
    private int _xMax;
    private int _yMin;
    private int _yMax;
    private int _zMin;
    private int _zMax;
    private int _procMax;

    public Territory(int terr) {
        _terr = terr;
        _xMin = 999999;
        _xMax = -999999;
        _yMin = 999999;
        _yMax = -999999;
        _zMin = 999999;
        _zMax = -999999;
        _procMax = 0;
    }

    public void add(int x, int y, int zmin, int zmax, int proc) {
        _points.add(new Point(x, y, zmin, zmax, proc));
        if (x < _xMin) {
            _xMin = x;
        }
        if (y < _yMin) {
            _yMin = y;
        }
        if (x > _xMax) {
            _xMax = x;
        }
        if (y > _yMax) {
            _yMax = y;
        }
        if (zmin < _zMin) {
            _zMin = zmin;
        }
        if (zmax > _zMax) {
            _zMax = zmax;
        }
        _procMax += proc;
    }

    public boolean isIntersect(int x, int y, Point p1, Point p2) {
        final double dy1 = p1._y - y;
        final double dy2 = p2._y - y;

        if (Math.abs(Math.signum(dy1) - Math.signum(dy2)) <= 1e-6) {
            return false;
        }

        final double dx1 = p1._x - x;
        final double dx2 = p2._x - x;

        if ((dx1 >= 0) && (dx2 >= 0)) {
            return true;
        }

        if ((dx1 < 0) && (dx2 < 0)) {
            return false;
        }

        final double dx0 = (dy1 * (p1._x - p2._x)) / (p1._y - p2._y);

        return dx0 <= dx1;
    }

    public boolean isInside(int x, int y) {
        int intersect_count = 0;
        for (int i = 0; i < _points.size(); i++) {
            final Point p1 = _points.get(i > 0 ? i - 1 : _points.size() - 1);
            final Point p2 = _points.get(i);

            if (isIntersect(x, y, p1, p2)) {
                intersect_count++;
            }
        }

        return (intersect_count % 2) == 1;
    }

    public Location getRandomPoint() {
        if (_procMax > 0) {
            int pos = 0;
            final int rnd = Rnd.get(_procMax);
            for (Point p1 : _points) {
                pos += p1._proc;
                if (rnd <= pos) {
                    return new Location(p1._x, p1._y, Rnd.get(p1._zmin, p1._zmax));
                }
            }
        }
        for (int i = 0; i < 100; i++) {
            final int x = Rnd.get(_xMin, _xMax);
            final int y = Rnd.get(_yMin, _yMax);
            if (isInside(x, y)) {
                double curdistance = 0;
                int zmin = _zMin;
                for (Point p1 : _points) {
                    double distance = Math.hypot(p1._x - x, p1._y - y);
                    if ((curdistance == 0) || (distance < curdistance)) {
                        curdistance = distance;
                        zmin = p1._zmin;
                    }
                }
                return new Location(x, y, Rnd.get(zmin, _zMax));
            }
        }
        LOGGER.warn("Can't make point for territory " + _terr);
        return null;
    }

    public int getProcMax() {
        return _procMax;
    }

    protected static class Point {
        protected int _x;
        protected int _y;
        protected int _zmin;
        protected int _zmax;
        protected int _proc;

        Point(int x, int y, int zmin, int zmax, int proc) {
            _x = x;
            _y = y;
            _zmin = zmin;
            _zmax = zmax;
            _proc = proc;
        }
    }
}
