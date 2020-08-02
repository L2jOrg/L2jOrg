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
package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.Buffer;

import static java.lang.Byte.toUnsignedInt;

/**
 * @author UnAfraid, Nos
 * @author JoeAlisson
 */
public class Crypt {
    private final byte[] inKey = new byte[16];
    private final byte[] outKey = new byte[16];
    private boolean enabled;


    public void setKey(byte[] key) {
        System.arraycopy(key, 0, inKey, 0, 16);
        System.arraycopy(key, 0, outKey, 0, 16);
    }

    public boolean encrypt(final Buffer data, final int offset, final int size) {
        if(!enabled) {
            enabled = true;
        } else {
            int encrypted = 0;
            for (int i = 0; i < size; i++) {
                int raw = toUnsignedInt(data.readByte(offset + i));
                encrypted = raw ^ outKey[i & 0x0F] ^ encrypted;
                data.writeByte(offset + i, (byte) encrypted);
            }

            shiftKey(outKey, size);
        }
        return true;
    }

    public boolean decrypt(Buffer data, int offset, int size) {
        if(enabled) {
            int xOr = 0;
            for(int i = 0; i < size; i++) {
                int encrypted = toUnsignedInt(data.readByte(offset + i));
                data.writeByte(offset + i, (byte) (encrypted ^ inKey[i & 15] ^ xOr));
                xOr  = encrypted;
            }
            shiftKey(inKey, size);
        }
        return true;
    }


    private void shiftKey(byte[] key, int size) {
        int old = key[8] & 0xff;
        old |= (key[9] << 8) & 0xff00;
        old |= (key[10] << 0x10) & 0xff0000;
        old |= (key[11] << 0x18) & 0xff000000;

        old += size;

        key[8] = (byte) (old & 0xff);
        key[9] = (byte) ((old >> 0x08) & 0xff);
        key[10] = (byte) ((old >> 0x10) & 0xff);
        key[11] = (byte) ((old >> 0x18) & 0xff);
    }
}