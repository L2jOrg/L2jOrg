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

import java.io.BufferedOutputStream;

/**
 * @author Hasha
 */
public class BlockNull extends ABlock {
    private final byte _nswe;

    public BlockNull() {
        _nswe = (byte) 0xFF;
    }

    @Override
    public final boolean hasGeoPos() {
        return false;
    }

    @Override
    public final short getHeightNearest(int geoX, int geoY, int worldZ) {
        return (short) worldZ;
    }

    @Override
    public final short getHeightNearestOriginal(int geoX, int geoY, int worldZ) {
        return (short) worldZ;
    }

    @Override
    public final short getHeightAbove(int geoX, int geoY, int worldZ) {
        return (short) worldZ;
    }

    @Override
    public final short getHeightBelow(int geoX, int geoY, int worldZ) {
        return (short) worldZ;
    }

    @Override
    public final byte getNsweNearest(int geoX, int geoY, int worldZ) {
        return _nswe;
    }

    @Override
    public final byte getNsweNearestOriginal(int geoX, int geoY, int worldZ) {
        return _nswe;
    }

    @Override
    public final byte getNsweAbove(int geoX, int geoY, int worldZ) {
        return _nswe;
    }

    @Override
    public final byte getNsweBelow(int geoX, int geoY, int worldZ) {
        return _nswe;
    }

    @Override
    public final int getIndexNearest(int geoX, int geoY, int worldZ) {
        return 0;
    }

    @Override
    public final int getIndexAbove(int geoX, int geoY, int worldZ) {
        return 0;
    }

    @Override
    public final int getIndexAboveOriginal(int geoX, int geoY, int worldZ) {
        return 0;
    }

    @Override
    public final int getIndexBelow(int geoX, int geoY, int worldZ) {
        return 0;
    }

    @Override
    public final int getIndexBelowOriginal(int geoX, int geoY, int worldZ) {
        return 0;
    }

    @Override
    public final short getHeight(int index) {
        return 0;
    }

    @Override
    public final short getHeightOriginal(int index) {
        return 0;
    }

    @Override
    public final byte getNswe(int index) {
        return _nswe;
    }

    @Override
    public final byte getNsweOriginal(int index) {
        return _nswe;
    }

    @Override
    public final void setNswe(int index, byte nswe) {
    }

    @Override
    public final void saveBlock(BufferedOutputStream stream) {
    }
}