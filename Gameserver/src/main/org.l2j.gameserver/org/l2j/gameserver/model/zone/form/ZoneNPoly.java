/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.zone.form;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.zone.L2ZoneForm;

import java.awt.*;

/**
 * A not so primitive npoly zone
 *
 * @author durgus
 */
public class ZoneNPoly extends L2ZoneForm {
    private final Polygon _p;
    private final int _z1;
    private final int _z2;

    /**
     * @param x
     * @param y
     * @param z1
     * @param z2
     */
    public ZoneNPoly(int[] x, int[] y, int z1, int z2) {
        _p = new Polygon(x, y, x.length);

        _z1 = Math.min(z1, z2);
        _z2 = Math.max(z1, z2);
    }

    @Override
    public boolean isInsideZone(int x, int y, int z) {
        return _p.contains(x, y) && (z >= _z1) && (z <= _z2);
    }

    @Override
    public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2) {
        return _p.intersects(Math.min(ax1, ax2), Math.min(ay1, ay2), Math.abs(ax2 - ax1), Math.abs(ay2 - ay1));
    }

    @Override
    public double getDistanceToZone(int x, int y) {
        final int[] _x = _p.xpoints;
        final int[] _y = _p.ypoints;
        double test;
        double shortestDist = Math.pow(_x[0] - x, 2) + Math.pow(_y[0] - y, 2);

        for (int i = 1; i < _p.npoints; i++) {
            test = Math.pow(_x[i] - x, 2) + Math.pow(_y[i] - y, 2);
            if (test < shortestDist) {
                shortestDist = test;
            }
        }

        return Math.sqrt(shortestDist);
    }

    // getLowZ() / getHighZ() - These two functions were added to cope with the demand of the new fishing algorithms, wich are now able to correctly place the hook in the water, thanks to getHighZ(). getLowZ() was added, considering potential future modifications.
    @Override
    public int getLowZ() {
        return _z1;
    }

    @Override
    public int getHighZ() {
        return _z2;
    }

    @Override
    public void visualizeZone(int z) {
        for (int i = 0; i < _p.npoints; i++) {
            final int nextIndex = (i + 1) == _p.xpoints.length ? 0 : i + 1;
            final int vx = _p.xpoints[nextIndex] - _p.xpoints[i];
            final int vy = _p.ypoints[nextIndex] - _p.ypoints[i];
            final float lenght = (float) Math.sqrt((vx * vx) + (vy * vy)) / STEP;
            for (int o = 1; o <= lenght; o++) {
                dropDebugItem(CommonItem.ADENA, 1, (int) (_p.xpoints[i] + ((o / lenght) * vx)), (int) (_p.ypoints[i] + ((o / lenght) * vy)), z);
            }
        }
    }

    @Override
    public Location getRandomPoint() {
        final int _minX = _p.getBounds().x;
        final int _maxX = _p.getBounds().x + _p.getBounds().width;
        final int _minY = _p.getBounds().y;
        final int _maxY = _p.getBounds().y + _p.getBounds().height;

        int x = Rnd.get(_minX, _maxX);
        int y = Rnd.get(_minY, _maxY);

        int antiBlocker = 0;
        while (!_p.contains(x, y) && (antiBlocker++ < 1000)) {
            x = Rnd.get(_minX, _maxX);
            y = Rnd.get(_minY, _maxY);
        }

        return new Location(x, y, GeoEngine.getInstance().getHeight(x, y, _z1));
    }

    public int[] getX() {
        return _p.xpoints;
    }

    public int[] getY() {
        return _p.ypoints;
    }
}
