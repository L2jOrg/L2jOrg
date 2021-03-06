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

public class ExRegenMax extends ServerPacket {
    private final int _time;
    private final int _tickInterval;
    private final double _amountPerTick;

    public ExRegenMax(int time, int tickInterval, double amountPerTick) {
        _time = time;
        _tickInterval = tickInterval;
        _amountPerTick = amountPerTick;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_REGEN_MAX, buffer );

        buffer.writeInt(1);
        buffer.writeInt(_time);
        buffer.writeInt(_tickInterval);
        buffer.writeDouble(_amountPerTick);
    }

}
