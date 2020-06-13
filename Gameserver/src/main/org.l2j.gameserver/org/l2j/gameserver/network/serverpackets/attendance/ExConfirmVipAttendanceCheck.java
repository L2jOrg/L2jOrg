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
package org.l2j.gameserver.network.serverpackets.attendance;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExConfirmVipAttendanceCheck extends ServerPacket {
    boolean _available;
    int _index;

    public ExConfirmVipAttendanceCheck(boolean rewardAvailable, int rewardIndex) {
        _available = rewardAvailable;
        _index = rewardIndex;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CONFIRM_VIP_ATTENDANCE_CHECK);
        writeByte((byte) (_available ? 0x01 : 0x00)); // can receive reward today? 1 else 0
        writeByte((byte) _index); // active reward index
        writeInt(0);
        writeInt(0);
    }

}
