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
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Hasha
 */
public class BlockMultilayer extends ABlock {
    private static final int MAX_LAYERS = Byte.MAX_VALUE;

    private static ByteBuffer temp;
    protected byte[] _buffer;

    /**
     * Implicit constructor for children class.
     */
    protected BlockMultilayer() {
        _buffer = null;
    }

    /**
     * Creates MultilayerBlock.
     *
     * @param bb     : Input byte buffer.
     * @param format : GeoFormat specifying format of loaded data.
     */
    public BlockMultilayer(ByteBuffer bb, GeoFormat format) {
        // move buffer pointer to end of MultilayerBlock
        for (int cell = 0; cell < GeoStructure.BLOCK_CELLS; cell++) {
            // get layer count for this cell
            final byte layers = format != GeoFormat.L2OFF ? bb.get() : (byte) bb.getShort();

            if ((layers <= 0) || (layers > MAX_LAYERS)) {
                throw new RuntimeException("Invalid layer count for MultilayerBlock");
            }

            // add layers count
            temp.put(layers);

            // loop over layers
            for (byte layer = 0; layer < layers; layer++) {
                if (format != GeoFormat.L2D) {
                    // get data
                    short data = bb.getShort();

                    // add nswe and height
                    temp.put((byte) (data & 0x000F));
                    temp.putShort((short) ((short) (data & 0xFFF0) >> 1));
                } else {
                    // add nswe
                    temp.put(bb.get());

                    // add height
                    temp.putShort(bb.getShort());
                }
            }
        }

        // initialize buffer
        _buffer = Arrays.copyOf(temp.array(), temp.position());

        // clear temp buffer
        temp.clear();
    }

    /**
     * Initializes the temporarily buffer.
     */
    public static void initialize() {
        // initialize temporarily buffer and sorting mechanism
        temp = ByteBuffer.allocate(GeoStructure.BLOCK_CELLS * MAX_LAYERS * 3).order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Releases temporarily buffer.
     */
    public static void release() {
        temp = null;
    }

    @Override
    public final boolean hasGeoPos() {
        return true;
    }

    @Override
    public final short getHeightNearest(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = getIndexNearest(geoX, geoY, worldZ);

        // get height
        return (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));
    }

    @Override
    public short getHeightNearestOriginal(int geoX, int geoY, int worldZ) {
        return getHeightNearest(geoX, geoY, worldZ);
    }

    @Override
    public final short getHeightAbove(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to last layer data (first from bottom)
        byte layers = _buffer[index++];
        index += (layers - 1) * 3;

        // loop though all layers, find first layer above worldZ
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // layer height is higher than worldZ, return layer height
            if (height > worldZ) {
                return (short) height;
            }

            // move index to next layer
            index -= 3;
        }

        // none layer found, return minimum value
        return Short.MIN_VALUE;
    }

    @Override
    public final short getHeightBelow(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to first layer data (first from top)
        byte layers = _buffer[index++];

        // loop though all layers, find first layer below worldZ
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // layer height is lower than worldZ, return layer height
            if (height < worldZ) {
                return (short) height;
            }

            // move index to next layer
            index += 3;
        }

        // none layer found, return maximum value
        return Short.MAX_VALUE;
    }

    @Override
    public final byte getNsweNearest(int geoX, int geoY, int worldZ) {
        // get cell index
        final int index = getIndexNearest(geoX, geoY, worldZ);

        // get nswe
        return _buffer[index];
    }

    @Override
    public byte getNsweNearestOriginal(int geoX, int geoY, int worldZ) {
        return getNsweNearest(geoX, geoY, worldZ);
    }

    @Override
    public final byte getNsweAbove(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to last layer data (first from bottom)
        byte layers = _buffer[index++];
        index += (layers - 1) * 3;

        // loop though all layers, find first layer above worldZ
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // layer height is higher than worldZ, return layer nswe
            if (height > worldZ) {
                return _buffer[index];
            }

            // move index to next layer
            index -= 3;
        }

        // none layer found, block movement
        return 0;
    }

    @Override
    public final byte getNsweBelow(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to first layer data (first from top)
        byte layers = _buffer[index++];

        // loop though all layers, find first layer below worldZ
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // layer height is lower than worldZ, return layer nswe
            if (height < worldZ) {
                return _buffer[index];
            }

            // move index to next layer
            index += 3;
        }

        // none layer found, block movement
        return 0;
    }

    @Override
    public final int getIndexNearest(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to first layer data (first from bottom)
        byte layers = _buffer[index++];

        // loop though all cell layers, find closest layer
        int limit = Integer.MAX_VALUE;
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // get Z distance and compare with limit
            // note: When 2 layers have same distance to worldZ (worldZ is in the middle of them):
            // > returns bottom layer
            // >= returns upper layer
            final int distance = Math.abs(height - worldZ);
            if (distance > limit) {
                break;
            }

            // update limit and move to next layer
            limit = distance;
            index += 3;
        }

        // return layer index
        return index - 3;
    }

    @Override
    public final int getIndexAbove(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to last layer data (first from bottom)
        byte layers = _buffer[index++];
        index += (layers - 1) * 3;

        // loop though all layers, find first layer above worldZ
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // layer height is higher than worldZ, return layer index
            if (height > worldZ) {
                return index;
            }

            // move index to next layer
            index -= 3;
        }

        // none layer found
        return -1;
    }

    @Override
    public int getIndexAboveOriginal(int geoX, int geoY, int worldZ) {
        return getIndexAbove(geoX, geoY, worldZ);
    }

    @Override
    public final int getIndexBelow(int geoX, int geoY, int worldZ) {
        // move index to the cell given by coordinates
        int index = 0;
        for (int i = 0; i < (((geoX % GeoStructure.BLOCK_CELLS_X) * GeoStructure.BLOCK_CELLS_Y) + (geoY % GeoStructure.BLOCK_CELLS_Y)); i++) {
            // move index by amount of layers for this cell
            index += (_buffer[index] * 3) + 1;
        }

        // get layers count and shift to first layer data (first from top)
        byte layers = _buffer[index++];

        // loop though all layers, find first layer below worldZ
        while (layers-- > 0) {
            // get layer height
            final int height = (_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8);

            // layer height is lower than worldZ, return layer index
            if (height < worldZ) {
                return index;
            }

            // move index to next layer
            index += 3;
        }

        // none layer found
        return -1;
    }

    @Override
    public int getIndexBelowOriginal(int geoX, int geoY, int worldZ) {
        return getIndexBelow(geoX, geoY, worldZ);
    }

    @Override
    public final short getHeight(int index) {
        // get height
        return (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));
    }

    @Override
    public short getHeightOriginal(int index) {
        // get height
        return (short) ((_buffer[index + 1] & 0x00FF) | (_buffer[index + 2] << 8));
    }

    @Override
    public final byte getNswe(int index) {
        // get nswe
        return _buffer[index];
    }

    @Override
    public byte getNsweOriginal(int index) {
        // get nswe
        return _buffer[index];
    }

    @Override
    public final void setNswe(int index, byte nswe) {
        // set nswe
        _buffer[index] = nswe;
    }

    @Override
    public final void saveBlock(BufferedOutputStream stream) throws IOException {
        // write block type
        stream.write(GeoStructure.TYPE_MULTILAYER_L2D);

        // for each cell
        int index = 0;
        for (int i = 0; i < GeoStructure.BLOCK_CELLS; i++) {
            // write layers count
            byte layers = _buffer[index++];
            stream.write(layers);

            // write cell data
            stream.write(_buffer, index, layers * 3);

            // move index to next cell
            index += layers * 3;
        }
    }
}