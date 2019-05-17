package org.l2j.gameserver.engines.captcha;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * https://docs.microsoft.com/pt-br/windows/desktop/direct3d10/d3d10-graphics-programming-guide-resources-block-compression#compression-algorithms
 *
 * //RGB packed 565 16 bit no alpha
 *  A = 255
 *  R = (color & 0xF800) >> 11
 *  G = (color & 0x7E0) >> 5
 *  B = (color & 0x1F)
 *
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.215.7942&rep=rep1&type=pdf
 *
 */
class DXT1ImageCompressor {

    private static final int MAGIC = 0x20534444;
    private static final int HEADER_SIZE = 124;
    private static final int DDSD_PIXELFORMAT = 0x1000;
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

        var data = image.getRaster();

        var texelBuffer = new short[16];

        // compress 4x4 Block
        for (int i = 0; i < height; i += 4) {
            for (int j = 0; j < width; j += 4) {
                extractBlock(data, j, i, texelBuffer);
                compressBlock(texelBuffer, buffer);
            }
        }
        return buffer.array();
    }

    private void compressBlock(short[] texelBuffer, ByteBuffer buffer) {
        var maxDistance = -1;
        short color0 = 0;
        short color1 = 0;

        // Choose the max e min colors
        for (int i = 0; i < 15; i++) {
            for (int j = i; j < 16; j++) {
                var distance = euclidianDistance(texelBuffer[i], texelBuffer[j]);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    color0 = texelBuffer[i];
                    color1 = texelBuffer[j];
                }
            }
        }

        if (color0 < color1) {
            var tmp = color0;
            color0 = color1;
            color1 = tmp;
        }

        short color2 = (short) (2.f / 3 * color0 + 1.f / 3 * color1);
        short color3 = (short) (1.f / 3 * color0 + 2.f / 3 * color1);

        // calculate encoded colors
        long encodedColors = encodeColors(texelBuffer, color0, color1, color2, color3);
        buffer.putShort(color0);
        buffer.putShort(color1);
        buffer.putInt((int) encodedColors);
    }

    private long encodeColors(final short[] texelBuffer, final short color0, final short color1, final short color2, final short color3) {
        long encodedColors = 0L;
        long bitMask;
        for (int i = 0; i < 16; i++) {
            var distance0 = euclidianDistance(color0, texelBuffer[i]);
            var distance1 = euclidianDistance(color1, texelBuffer[i]);
            var distance2 = euclidianDistance(color2, texelBuffer[i]);
            var distance3 = euclidianDistance(color3, texelBuffer[i]);

            int mask0 = maskOf(distance0, distance3);
            int mask1 = maskOf(distance1, distance2);
            int mask2 = maskOf(distance0, distance2);
            int mask3 = maskOf(distance1, distance3);
            int mask4 = maskOf(distance2, distance3);

            int bitmask0 = mask1 & mask2;
            int bitmask1 = mask0 & mask3;
            int bitmask2 = mask0 & mask4;

            bitMask = (bitmask2 | ((bitmask0 | bitmask1) << 1));

            encodedColors |= (bitMask << (i << 1));
        }
        return encodedColors;
    }

    /* If a is greater than b, than b-a will be a negative value, and the
      32nd bit will be a one. Otherwise, b-a will be a positive value or zero, and the 32nd bit will be a zero.
      Therefore we need only return the 32nd bit of the value b-a. */
    private int maskOf(int a, int b) {
        return (a - b) >>> 31;
    }

    private int euclidianDistance(short x, short y) {
        return (x - y) * (x - y);
    }

    private void writeHeader(BufferedImage image, ByteBuffer buffer) {
        buffer.putInt(MAGIC);
        buffer.putInt(HEADER_SIZE);
        buffer.putInt(DDSD_CAPS | DDSD_HEIGHT | DDSD_PIXELFORMAT | DDSD_WIDTH);
        buffer.putInt(image.getHeight());
        buffer.putInt(image.getWidth());
        buffer.putInt((image.getWidth() * image.getHeight()) / 2); // Pitch Or Linear Size
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

    private void extractBlock(Raster image, int x, int y, short[] textelBuffer) {
        int blockWidth = Math.min(image.getWidth() - x, 4);
        int blockHeight = Math.min(image.getHeight() - y, 4);

        image.getDataElements(x, y, blockWidth, blockHeight, textelBuffer);

    }
}
