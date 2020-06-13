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
package org.l2j.gameserver.engine.geo.geodata;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.Location;

/**
 * @author Hasha
 */
public class GeoLocation extends Location {
    private byte _nswe;

    public GeoLocation(int x, int y, int z) {
        super(x, y, GeoEngine.getInstance().getHeightNearest(x, y, z));
        _nswe = GeoEngine.getInstance().getNsweNearest(x, y, z);
    }

    public void set(int x, int y, short z) {
        super.setXYZ(x, y, GeoEngine.getInstance().getHeightNearest(x, y, z));
        _nswe = GeoEngine.getInstance().getNsweNearest(x, y, z);
    }

    public int getGeoX() {
        return _x;
    }

    public int getGeoY() {
        return _y;
    }

    @Override
    public int getX() {
        return GeoEngine.getWorldX(_x);
    }

    @Override
    public int getY() {
        return GeoEngine.getWorldY(_y);
    }

    public byte getNSWE() {
        return _nswe;
    }
}