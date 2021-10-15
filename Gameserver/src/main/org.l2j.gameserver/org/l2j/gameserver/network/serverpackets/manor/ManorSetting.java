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
package org.l2j.gameserver.network.serverpackets.manor;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public abstract class ManorSetting extends ServerPacket {

    protected void writeSeeds(WritableBuffer buffer, int manorId, Collection<Object> seeds) {
        buffer.writeInt(manorId); // manor id
        buffer.writeInt(seeds.size()); // size

        // for each seed
        buffer.writeInt(0x00); // seed id
        buffer.writeInt(0x00); // level
        buffer.writeByte(1);
        buffer.writeInt(0x00); // reward 1 id
        buffer.writeByte(1);
        buffer.writeInt(0x00); // reward 2 id
        buffer.writeInt(0x00); // next sale limit
        buffer.writeInt(0x00); // price for castle to produce 1
        buffer.writeInt(0x00); // min seed price
        buffer.writeInt(0x00); // max seed price
        // Current period
        writeCurrentPeriod(buffer);
        // Next period
        writeNextPeriod(buffer);
    }

    protected void writeNextPeriod(WritableBuffer buffer) {
        buffer.writeLong(0); // start amount sale
        buffer.writeLong(0); // price
    }

    protected void writeCurrentPeriod(WritableBuffer buffer) {
        buffer.writeLong(0); // start amount
        buffer.writeLong(0); // price
    }
}
