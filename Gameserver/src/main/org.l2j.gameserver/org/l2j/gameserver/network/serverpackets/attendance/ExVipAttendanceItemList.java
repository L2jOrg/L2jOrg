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

import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExVipAttendanceItemList extends ServerPacket {
    boolean _available;
    int _index;

    public ExVipAttendanceItemList(Player player) {
        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        _available = attendanceInfo.isRewardAvailable();
        _index = attendanceInfo.getRewardIndex();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_VIP_ATTENDANCE_ITEM_LIST);
        writeByte((byte) (_available ? _index + 1 : _index)); // index to receive?
        writeByte((byte) _index); // last received index?
        writeInt(0x00);
        writeInt(0x00);
        writeByte((byte) 0x01);
        writeByte((byte) (_available ? 0x01 : 0x00)); // player can receive reward today?
        writeByte((byte) 250);
        writeByte((byte) AttendanceRewardData.getInstance().getRewardsCount()); // reward size
        int rewardCounter = 0;
        for (ItemHolder reward : AttendanceRewardData.getInstance().getRewards()) {
            rewardCounter++;
            writeInt(reward.getId());
            writeLong(reward.getCount());
            writeByte((byte) 0x01); // is unknown?
            writeByte((byte) ((rewardCounter % 7) == 0 ? 0x01 : 0x00)); // is last in row?
        }
        writeByte((byte) 0x00);
        writeInt(0x00);
    }

}
