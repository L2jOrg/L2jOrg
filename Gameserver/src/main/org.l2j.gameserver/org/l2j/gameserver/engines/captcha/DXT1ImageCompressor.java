package org.l2j.gameserver.engines.captcha;


import java.awt.image.BufferedImage;

/*
 * https://docs.microsoft.com/pt-br/windows/desktop/direct3d10/d3d10-graphics-programming-guide-resources-block-compression#compression-algorithms
 *
 * //RGB packed 565 16 bit no alpha
 *  A = 255
 *  R = color & 0xf800 >> 11
 *  G = color & 0x7e0 >> 5
 *  B = color & 0x1F
 *
 *
 */
public class DXT1ImageCompressor {

    public static byte[] compress(BufferedImage image) {

        TextureBlock block = new TextureBlock();

        var height = image.getHeight();
        var width = image.getWidth();

        var textelBuffer = new short[image.getData().getNumDataElements() * 16];

        // compress 4x4 Block
        for (int i = 0; i < height; i+=4) {
            for (int j = 0; j < width; j+=4) {

                extractBlock(image, j, i, textelBuffer);
            }

        }




        return null;
    }

    private static void extractBlock(BufferedImage image, int x, int y, short[] textelBuffer) {
        int blockWidth = Math.min(image.getWidth()- x, 4);
        int blockHeight = Math.min(image.getHeight()- y, 4);

        image.getData().getDataElements(x, y, blockWidth, blockHeight, textelBuffer);
    }
}
