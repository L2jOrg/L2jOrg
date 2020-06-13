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
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Hasha
 */
public class BlockFlat extends ABlock {
    protected final short _height;
    protected byte _nswe;

    /**
     * Creates FlatBlock.
     *
     * @param bb     : Input byte buffer.
     * @param format : GeoFormat specifying format of loaded data.
     */
    public BlockFlat(ByteBuffer bb, GeoFormat format) {
        _height = bb.getShort();
        _nswe = format != GeoFormat.L2D ? 0x0F : (byte) (0xFF);

        if (format == GeoFormat.L2OFF) {
            bb.getShort();
        }
    }

    @Override
    public final boolean hasGeoPos() {
        return true;
    }

    @Override
    public final short getHeightNearest(int geoX, int geoY, int worldZ) {
        return _height;
    }

    @Override
    public final short getHeightNearestOriginal(int geoX, int geoY, int worldZ) {
        return _height;
    }

    @Override
    public final short getHeightAbove(int geoX, int geoY, int worldZ) {
        // check and return height
        return _height > worldZ ? _height : Short.MIN_VALUE;
    }

    @Override
    public final short getHeightBelow(int geoX, int geoY, int worldZ) {
        // check and return height
        return _height < worldZ ? _height : Short.MAX_VALUE;
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
        // check height and return nswe
        return _height > worldZ ? _nswe : 0;
    }

    @Override
    public final byte getNsweBelow(int geoX, int geoY, int worldZ) {
        // check height and return nswe
        return _height < worldZ ? _nswe : 0;
    }

    @Override
    public final int getIndexNearest(int geoX, int geoY, int worldZ) {
        return 0;
    }

    @Override
    public final int getIndexAbove(int geoX, int geoY, int worldZ) {
        // check height and return index
        return _height > worldZ ? 0 : -1;
    }

    @Override
    public final int getIndexAboveOriginal(int geoX, int geoY, int worldZ) {
        return getIndexAbove(geoX, geoY, worldZ);
    }

    @Override
    public final int getIndexBelow(int geoX, int geoY, int worldZ) {
        // check height and return index
        return _height < worldZ ? 0 : -1;
    }

    @Override
    public final int getIndexBelowOriginal(int geoX, int geoY, int worldZ) {
        return getIndexBelow(geoX, geoY, worldZ);
    }

    @Override
    public final short getHeight(int index) {
        return _height;
    }

    @Override
    public final short getHeightOriginal(int index) {
        return _height;
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
        _nswe = nswe;
    }

    @Override
    public final void saveBlock(BufferedOutputStream stream) throws IOException {
        // write block type
        stream.write(GeoStructure.TYPE_FLAT_L2D);

        // write height
        stream.write((byte) (_height & 0x00FF));
        stream.write((byte) (_height >> 8));
    }
}