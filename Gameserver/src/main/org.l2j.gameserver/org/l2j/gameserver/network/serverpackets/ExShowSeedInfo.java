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
import java.util.List;

/**
 * @author l3x
 */
public class ExShowSeedInfo extends ServerPacket {
    private final List<Object> seeds = Collections.emptyList();
    private final int manorId;
    private final boolean hideButtons;

    public ExShowSeedInfo(int manorId, boolean hideButtons) {
        this.manorId = manorId;
        this.hideButtons = hideButtons;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_SEED_INFO, buffer );

        buffer.writeByte(hideButtons); // Hide "Seed Purchase" button
        buffer.writeInt(manorId); // Manor ID
        buffer.writeInt(0x00); // Unknown

        buffer.writeInt(seeds.size());
        // for each seed
            buffer.writeInt(0x00); // Seed id
            buffer.writeLong(0x00); // amount Left to buy
            buffer.writeLong(0x00); // Started amount
            buffer.writeLong(0x00); // Sell Price

            buffer.writeInt(0); // Seed level
            buffer.writeByte(0x01); // Reward 1
            buffer.writeInt(0); // Reward 1 - item id
            buffer.writeByte(0x01); // Reward 2
            buffer.writeInt(0); // Reward 2 - item id
    }

}