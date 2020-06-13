/*
 * Copyright © 2019 L2J Mobius
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

/**
 * @author Hasha
 */
public interface IGeoObject {
    /**
     * Returns geodata X coordinate of the {@link IGeoObject}.
     *
     * @return int : Geodata X coordinate.
     */
    int getGeoX();

    /**
     * Returns geodata Y coordinate of the {@link IGeoObject}.
     *
     * @return int : Geodata Y coordinate.
     */
    int getGeoY();

    /**
     * Returns geodata Z coordinate of the {@link IGeoObject}.
     *
     * @return int : Geodata Z coordinate.
     */
    int getGeoZ();

    /**
     * Returns height of the {@link IGeoObject}.
     *
     * @return int : Height.
     */
    int getHeight();

    /**
     * Returns {@link IGeoObject} data.
     *
     * @return byte[][] : {@link IGeoObject} data.
     */
    byte[][] getObjectGeoData();
}
