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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.ServerType.CLASSIC;

public final class KeyPacket extends ServerPacket {
    private final byte[] _key;
    private final int _result;

    public KeyPacket(byte[] key, int result) {
        _key = key;
        _result = result;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.VERSION_CHECK);

        writeByte(_result); // 0 - wrong protocol, 1 - protocol ok
        for (int i = 0; i < 8; i++) {
            writeByte(_key[i]); // key
        }
        var serverSettings = getSettings(ServerSettings.class);
        writeInt(true); // cipher enabled
        writeInt(serverSettings.serverId());
        writeByte(false); // merged server
        writeInt(0x00); // obfuscation key
        writeByte((serverSettings.type() & CLASSIC.getMask()) != 0); // isClassic
        writeByte(0x00); // queued ?
    }

}
