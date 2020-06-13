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

/**
 * @author JoeAlisson
 */
class TextureBlock {

    private ARGB[] colors = new ARGB[16];
    private ARGB[] palette = new ARGB[4];
    private int minColorIndex;
    private int maxColorIndex;
    private short minColor;
    private short maxColor;


    TextureBlock() {
        for (int i = 0; i < 16; i++) {
            colors[i] = new ARGB();
        }

        for (int i = 0; i < 4; i++) {
            palette[i] = new ARGB();
        }
    }

    public void of(int[] buffer) {
        var maxDistance = -1;

        for (int i = 0; i < 16; i++) {
            colors[i].a = (0xFF & (buffer[i] >> 24));
            colors[i].r = (0xFF & (buffer[i] >> 16));
            colors[i].g = (0xFF & (buffer[i] >> 8));
            colors[i].b = (0xFF & (buffer[i]));

            for (int j = i-1; j >=0; j--) {
                var distance = euclidianDistance(colors[i], colors[j]);
                if(distance > maxDistance) {
                    maxDistance = distance;
                    minColorIndex = j;
                    maxColorIndex = i;
                }
            }
        }

        computMinMaxColor();
        computePalette();

    }

    private void computePalette() {
        palette[0] = colorAt(maxColorIndex);
        palette[1] = colorAt(minColorIndex);

        palette[2].a = 255;
        palette[2].r = (2* palette[0].r + palette[1].r) / 3;
        palette[2].g = (2* palette[0].g + palette[1].g) / 3;
        palette[2].b = (2* palette[0].b + palette[1].b) / 3;

        palette[3].a = 255;
        palette[3].r = (2* palette[1].r + palette[0].r) / 3;
        palette[3].g = (2* palette[1].g + palette[0].g) / 3;
        palette[3].b = (2* palette[1].b + palette[0].b) / 3;
    }

    private void computMinMaxColor() {
        maxColor = colors[maxColorIndex].toShortRGB565();
        minColor = colors[minColorIndex].toShortRGB565();

        if(maxColor < minColor) {
            var tmp = maxColor;
            maxColor = minColor;
            minColor = tmp;

            var tmp2 = maxColorIndex;
            maxColorIndex = minColorIndex;
            minColorIndex = tmp2;
        }
    }

    private int euclidianDistance(ARGB c1, ARGB c2) {
        return (c1.r - c2.r) * (c1.r - c2.r) + (c1.g - c2.g) * (c1.g - c2.g) + (c1.b - c2.b) * (c1.b - c2.b);
    }

    short getMaxColor() {
        return maxColor;
    }

    short getMinColor() {
        return minColor;
    }

    ARGB colorAt(int index) {
        return colors[index];
    }

    ARGB[] getPalette() {
        return palette;
    }

    static class ARGB {
        int a;
        int r;
        int g;
        int b;

        short toShortRGB565() {
            return (short) (((0xF8 & r) << 8) | ((0xFC & g) << 3) | (b >> 3));
        }
    }
}
