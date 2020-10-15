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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExVipAttendanceItemList extends ServerPacket {
    boolean available;
    byte index;

    public ExVipAttendanceItemList(Player player) {
        available = player.canReceiveAttendance();
        index = player.lastAttendanceReward();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_VIP_ATTENDANCE_ITEM_LIST, buffer );
        buffer.writeByte(available ? index + 1 : index); // index to receive?
        buffer.writeByte(index); // last received index?
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeByte(0x01);
        buffer.writeByte(available); // player can receive reward today?
        buffer.writeByte(250);
        buffer.writeByte(AttendanceRewardData.getInstance().getRewardsCount()); // reward size
        int rewardCounter = 0;
        for (ItemHolder reward : AttendanceRewardData.getInstance().getRewards()) {
            rewardCounter++;
            buffer.writeInt(reward.getId());
            buffer.writeLong(reward.getCount());
            buffer.writeByte(0x01); // is unknown?
            buffer.writeByte(rewardCounter % 7 == 0); // is last in row?
        }
        buffer.writeByte( 0x00);
        buffer.writeInt(0x00);
    }

}
