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

import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.server.OnPacketReceived;
import org.l2j.gameserver.model.events.impl.server.OnPacketSent;

import static java.lang.Byte.toUnsignedInt;

/**
 * @author UnAfraid, Nos
 */
public class Crypt {
    private final GameClient _client;
    private final byte[] _inKey = new byte[16];
    private final byte[] outKey = new byte[16];
    private boolean _isEnabled;

    public Crypt(GameClient client) {
        _client = client;
    }

    public void setKey(byte[] key) {
        System.arraycopy(key, 0, _inKey, 0, 16);
        System.arraycopy(key, 0, outKey, 0, 16);
    }

    public byte[] encrypt(final byte[] data, final int offset, final int size) {
        if(!_isEnabled) {
            _isEnabled = true;
            onPacketSent(data);
            return data;
        }

        onPacketSent(data);

        int encrypted = 0;
        for (int i = 0; i < size; i++) {
            int raw = toUnsignedInt(data[offset + i]);
            encrypted =  raw ^ outKey[i & 0x0F] ^ encrypted;
            data[offset + i] = (byte) encrypted;
        }

        shiftKey(outKey, size);
        return data;
    }

    public boolean decrypt(byte[] data, int offset, int size) {
        if(!_isEnabled) {
            onPacketReceive(data);
            return true;
        }

        int xOr = 0;
        for(int i = 0; i < size; i++) {
            int encrypted =  toUnsignedInt(data[offset + i]);
            data[offset + i] = (byte) (encrypted ^ _inKey[i & 15] ^ xOr);
            xOr  = encrypted;
        }

        shiftKey(_inKey, size);
        onPacketReceive(data);
        return true;

    }

    private void onPacketSent(byte[] data) {
        EventDispatcher.getInstance().notifyEvent(new OnPacketSent(_client, data));
    }

    private void onPacketReceive(byte[] data) {
        EventDispatcher.getInstance().notifyEvent(new OnPacketReceived(_client, data));
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
