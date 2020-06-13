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
package org.l2j.gameserver.engine.captcha;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.lang.Math.abs;

/**
 * https://docs.microsoft.com/pt-br/windows/desktop/direct3d10/d3d10-graphics-programming-guide-resources-block-compression#compression-algorithms
 *
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.215.7942&rep=rep1&type=pdf
 *
 * @author joeAlisson
 */
class DXT1ImageCompressor {

    private static final int MAGIC = 0x20534444;
    private static final int HEADER_SIZE = 124;
    private static final int DDSD_PIXEL_FORMAT = 0x1000;
    private static final int DDSD_CAPS = 0x01;
    private static final int DDSD_HEIGHT = 0x02;
    private static final int DDSD_WIDTH = 0x04;

    private static final int DDSCAPS_TEXTURE = 0x1000;

    private static final int DDPF_FOURCC = 0x04;
    private static final int DXT1 = 0X31545844;
    private static final int PIXEL_FORMAT_SIZE = 32;

    byte[] compress(BufferedImage image) {
        var height = image.getHeight();
        var width = image.getWidth();

        var compressedSize = Math.max(width, 4) * Math.max(height, 4) / 2;
        var buffer = ByteBuffer.allocate(HEADER_SIZE + 4 + compressedSize).order(ByteOrder.LITTLE_ENDIAN);

        writeHeader(image, buffer);

        var texelBuffer = new int[16];
        TextureBlock block = new TextureBlock();

        // compress 4x4 Block
        for (int i = 0; i < height; i += 4) {
            for (int j = 0; j < width; j += 4) {
                extractBlock(image, j, i, texelBuffer, block);

                buffer.putShort(block.getMaxColor());
                buffer.putShort(block.getMinColor());
                buffer.putInt(computColorIndexes(block));
            }
        }
        return buffer.array();
    }

    private int computColorIndexes(final TextureBlock block) {
        var palette = block.getPalette();

        long encodedColors = 0;
        long index;
        for (int i = 15; i >= 0; i--) {

            var color = block.colorAt(i);

            int d0 = abs(palette[0].r - color.r) + abs(palette[0].g - color.g) + abs(palette[0].b - color.b);
            int d1 = abs(palette[1].r - color.r) + abs(palette[1].g - color.g) + abs(palette[1].b - color.b);
            int d2 = abs(palette[2].r - color.r) + abs(palette[2].g - color.g) + abs(palette[2].b - color.b);
            int d3 = abs(palette[3].r - color.r) + abs(palette[3].g - color.g) + abs(palette[3].b - color.b);

            int b0 = compare(d0, d3);
            int b1 = compare(d1, d2);
            int b2 = compare(d0, d2);
            int b3 = compare(d1, d3);
            int b4 = compare(d2, d3);

            int x0 = b1 & b2;
            int x1 = b0 & b3;
            int x2 = b0 & b4;

            index = (x2 | ((x0 | x1) << 1));
            encodedColors |= (index << (i << 1));
        }
        return (int) encodedColors;
    }

    /*
     *  return 1 if a > b, 0 otherwise
     */
    private int compare(int a, int b) {
        return (b-a) >>> 31;
    }

    private void writeHeader(BufferedImage image, ByteBuffer buffer) {
        buffer.putInt(MAGIC);
        buffer.putInt(HEADER_SIZE);
        buffer.putInt(DDSD_CAPS | DDSD_HEIGHT | DDSD_PIXEL_FORMAT | DDSD_WIDTH);
        buffer.putInt(image.getHeight());
        buffer.putInt(image.getWidth());
        buffer.putInt(0x00); // Pitch Or Linear Size
        buffer.putInt(0x00); // Depth
        buffer.putInt(0x00); // MipMapCount
        buffer.put(new byte[44]); // Reserved not used

        // Pixel Format
        buffer.putInt(PIXEL_FORMAT_SIZE); // Scructure size
        buffer.putInt(DDPF_FOURCC);
        buffer.putInt(DXT1);  // Format DXT for now only dxt1
        buffer.putInt(0x00); // RGB bit count
        buffer.putInt(0x00); // Red bit mask
        buffer.putInt(0x00); // Green bit mask
        buffer.putInt(0x00); // Blue bit mask
        buffer.putInt(0x00); // Alpha bit mask
        // End pixel format
        buffer.putInt(DDSCAPS_TEXTURE); // Complexity Caps
        buffer.putInt(0x00); // caps 2
        buffer.putInt(0x00); // caps 3
        buffer.putInt(0x00); // caps 4
        buffer.putInt(0x00); // Reserved 2 not used
    }

    private void extractBlock(final BufferedImage image, int x, int y, final int[] buffer, final TextureBlock block) {
        int blockWidth = Math.min(image.getWidth() - x, 4);
        int blockHeight = Math.min(image.getHeight() - y, 4);

        image.getRGB(x, y, blockWidth, blockHeight, buffer, 0, 4);
        block.of(buffer);
    }
}
