/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.settings.ServerSettings;

/**
 * @author -Wooden-, Sdw
 */
public class ExPledgeEmblem extends ServerPacket {
    private static final int TOTAL_SIZE = 65664;
    private final int _crestId;
    private final int _clanId;
    private final byte[] _data;
    private final int _chunkId;

    public ExPledgeEmblem(int crestId, byte[] chunkedData, int clanId, int chunkId) {
        _crestId = crestId;
        _data = chunkedData;
        _clanId = clanId;
        _chunkId = chunkId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_EMBLEM, buffer );

        buffer.writeInt(ServerSettings.serverId());
        buffer.writeInt(_clanId);
        buffer.writeInt(_crestId);
        buffer.writeInt(_chunkId);
        buffer.writeInt(TOTAL_SIZE);
        if (_data != null) {
            buffer.writeInt(_data.length);
            buffer.writeBytes(_data);
        } else {
            buffer.writeInt(0);
        }
    }

}