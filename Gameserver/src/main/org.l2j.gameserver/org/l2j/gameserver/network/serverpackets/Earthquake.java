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
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class Earthquake extends ServerPacket {
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _intensity;
    private final int _duration;

    /**
     * @param location
     * @param intensity
     * @param duration
     */
    public Earthquake(ILocational location, int intensity, int duration) {
        _x = location.getX();
        _y = location.getY();
        _z = location.getZ();
        _intensity = intensity;
        _duration = duration;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param intensity
     * @param duration
     */
    public Earthquake(int x, int y, int z, int intensity, int duration) {
        _x = x;
        _y = y;
        _z = z;
        _intensity = intensity;
        _duration = duration;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.EARTHQUAKE, buffer );

        buffer.writeInt(_x);
        buffer.writeInt(_y);
        buffer.writeInt(_z);
        buffer.writeInt(_intensity);
        buffer.writeInt(_duration);
        buffer.writeInt(0x00); // Unknown
    }

}
