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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collections;
import java.util.Set;

/**
 * @author l3x
 */
public class ExShowCropSetting extends ServerPacket {
    private final int _manorId;
    private final Set<Object> seeds = Collections.emptySet();

    public ExShowCropSetting(int manorId) {
        _manorId = manorId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_CROP_SETTING, buffer );

        buffer.writeInt(_manorId); // manor id
        buffer.writeInt(seeds.size()); // size

        // for each seed
            buffer.writeInt(0x00); // crop id
            buffer.writeInt(0x00); // seed level
            buffer.writeByte(1);
            buffer.writeInt(0x00); // reward 1 id
            buffer.writeByte(1);
            buffer.writeInt(0x00); // reward 2 id
            buffer.writeInt(0x00); // next sale limit
            buffer.writeInt(0); // ???
            buffer.writeInt(0x00); // min crop price
            buffer.writeInt(0x00); // max crop price
            // Current period

            buffer.writeLong(0); // start amount
            buffer.writeLong(0); // price
            buffer.writeByte(0); // reward

            // Next period
            buffer.writeLong(0); // start amount
            buffer.writeLong(0); // price
            buffer.writeByte(0); // reward
    }

}