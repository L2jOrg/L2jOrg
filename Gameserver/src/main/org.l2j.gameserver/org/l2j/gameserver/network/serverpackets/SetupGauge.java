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

public final class SetupGauge extends ServerPacket {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int CYAN = 2;

    private final int _dat1;
    private final int _time;
    private final int _time2;
    private final int _charObjId;

    public SetupGauge(int objectId, int dat1, int time) {
        _charObjId = objectId;
        _dat1 = dat1; // color 0-blue 1-red 2-cyan 3-green
        _time = time;
        _time2 = time;
    }

    public SetupGauge(int objectId, int color, int currentTime, int maxTime) {
        _charObjId = objectId;
        _dat1 = color; // color 0-blue 1-red 2-cyan 3-green
        _time = currentTime;
        _time2 = maxTime;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SETUP_GAUGE);
        writeInt(_charObjId);
        writeInt(_dat1);
        writeInt(_time);
        writeInt(_time2);
    }

}
