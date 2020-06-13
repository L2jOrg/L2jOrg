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
public class BlockComplex extends ABlock {
    protected byte[] _buffer;

    /**
     * Implicit constructor for children class.
     */
    protected BlockComplex() {
        // buffer is initialized in children class
        _buffer = null;
    }

    /**
     * Creates ComplexBlock.
     *
     * @param bb     : Input byte buffer.
     * @param format : GeoFormat specifying format of loaded data.
     */
    public BlockComplex(ByteBuffer bb, GeoFormat format) {
        // initialize buffer
        _buffer = new byte[GeoStructure.BLOCK_CELLS * 3];

        // load data
        for (int i = 0; i < GeoStructure.BLOCK_CELLS; i++) {
            if (format != GeoFormat.L2D) {
                // get data
                short data = bb.getShort();

                // get nswe
                _buffer[i * 3] = (byte) (data & 0x000F);

                // get height
                data = (short) ((short) (data & 0xFFF0) >> 1);
                _buffer[(i * 3) + 1] = (byte) (data & 0x00FF);
                _buffer[(i * 3) + 2] = (byte) (data >> 8);
            } else {
                // get nswe
                final byte nswe = bb.get();
                _buffer[i * 3] = nswe;

                // get height
                final short height = bb.getShort();
                _buffer[(i * 3) + 1] = (byte) (height & 0x00FF);
                _buffer[(i * 3) + 2] = (byte) (height >> 8);
            }
        }
    }

    @Override
    public final boolean hasGeoPos() {
        return true;
    }

    @Override
    public final short getHeightNearest(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        return (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));
    }

    @Override
    public short getHeightNearestOriginal(int geoX, int geoY, int worldZ) {
        return getHeightNearest(geoX, geoY, worldZ);
    }

    @Override
    public final short getHeightAbove(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        final short height = (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));

        // check and return height
        return height > worldZ ? height : Short.MIN_VALUE;
    }

    @Override
    public final short getHeightBelow(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        final short height = (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));

        // check and return height
        return height < worldZ ? height : Short.MAX_VALUE;
    }

    @Override
    public final byte getNsweNearest(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get nswe
        return _buffer[index];
    }

    @Override
    public byte getNsweNearestOriginal(int geoX, int geoY, int worldZ) {
        return getNsweNearest(geoX, geoY, worldZ);
    }

    @Override
    public final byte getNsweAbove(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

        // check height and return nswe
        return height > worldZ ? _buffer[index] : 0;
    }

    @Override
    public final byte getNsweBelow(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

        // check height and return nswe
        return height < worldZ ? _buffer[index] : 0;
    }

    @Override
    public final int getIndexNearest(int geoX, int geoY, int worldZ) {
        return (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;
    }

    @Override
    public final int getIndexAbove(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

        // check height and return nswe
        return height > worldZ ? index : -1;
    }

    @Override
    public int getIndexAboveOriginal(int geoX, int geoY, int worldZ) {
        return getIndexAbove(geoX, geoY, worldZ);
    }

    @Override
    public final int getIndexBelow(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)) * 3;

        // get height
        final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

        // check height and return nswe
        return height < worldZ ? index : -1;
    }

    @Override
    public int getIndexBelowOriginal(int geoX, int geoY, int worldZ) {
        return getIndexBelow(geoX, geoY, worldZ);
    }

    @Override
    public final short getHeight(int index) {
        return (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));
    }

    @Override
    public short getHeightOriginal(int index) {
        return (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));
    }

    @Override
    public final byte getNswe(int index) {
        return _buffer[index];
    }

    @Override
    public byte getNsweOriginal(int index) {
        return _buffer[index];
    }

    @Override
    public final void setNswe(int index, byte nswe) {
        _buffer[index] = nswe;
    }

    @Override
    public final void saveBlock(BufferedOutputStream stream) throws IOException {
        // write block type
        stream.write(GeoStructure.TYPE_COMPLEX_L2D);

        // write block data
        stream.write(_buffer, 0, GeoStructure.BLOCK_CELLS * 3);
    }
}