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
package org.l2j.gameserver.network.serverpackets.payback;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author JoeAlisson
 */
public class ExPaybackList extends ServerPacket {

    private final byte eventType;

    public ExPaybackList(byte eventType) {
        this.eventType = eventType;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_PAYBACK_LIST, buffer );

        buffer.writeInt(2); // payback size
        for (int i = 0; i < 2; i++) {

            buffer.writeInt(3); // reward set size
            for (int j = 0; j < 3; j++) {
                buffer.writeInt(57 + j); // item
                buffer.writeInt(1); // amount
            }

            buffer.writeByte(i); // set index
            buffer.writeInt(6); // requirement
            buffer.writeByte(5); // received
        }

        buffer.writeByte(eventType);
        buffer.writeInt((int) Instant.now().plus(6, ChronoUnit.HOURS).getEpochSecond()); // end datetime
        buffer.writeInt(57); // item consume
        buffer.writeInt(2); // user consumption



    }
}
