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
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 * @author UnAfraid
 */
public class ExPCCafePointInfo extends ServerPacket {
    private final int _points;
    private final int _mAddPoint;
    private final int _mPeriodType;
    private final int _remainTime;
    private final int _pointType;
    private final int _time;

    public ExPCCafePointInfo() {
        _points = 0;
        _mAddPoint = 0;
        _remainTime = 0;
        _mPeriodType = 0;
        _pointType = 0;
        _time = 0;
    }

    public ExPCCafePointInfo(int points, int pointsToAdd, int time) {
        _points = points;
        _mAddPoint = pointsToAdd;
        _mPeriodType = 1;
        _remainTime = 0; // No idea why but retail sends 42..
        _pointType = pointsToAdd < 0 ? 2 : 1; // When using points is 3
        _time = time;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PCCAFE_POINT_INFO);

        writeInt(_points); // num points
        writeInt(_mAddPoint); // points inc display
        writeByte((byte) _mPeriodType); // period(0=don't show window,1=acquisition,2=use points)
        writeInt(_remainTime); // period hours left
        writeByte((byte) _pointType); // points inc display color(0=yellow, 1=cyan-blue, 2=red, all other black)
        writeInt(_time * 3); // value is in seconds * 3
    }

}
