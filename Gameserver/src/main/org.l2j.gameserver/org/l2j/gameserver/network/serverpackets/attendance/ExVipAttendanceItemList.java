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
package org.l2j.gameserver.network.serverpackets.attendance;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.item.AttendanceEngine;
import org.l2j.gameserver.engine.item.AttendanceItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.settings.AttendanceSettings;

import java.util.List;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class ExVipAttendanceItemList extends ServerPacket {

    private final List<AttendanceItem> rewards;
    private final List<AttendanceItem> vipRewards;

    private final boolean available;
    private final byte lastReceived;
    private final byte nextReceive;
    private final int pcCafeMask;
    private final boolean vipAvailable;
    private final int minLevel;
    private final int vipRewardedMask;

    public ExVipAttendanceItemList(Player player) {
        final var engine = AttendanceEngine.getInstance();
        rewards = engine.getRewards();
        vipRewards = engine.getVipRewards();

        available = player.canReceiveAttendance() && !rewards.isEmpty();
        vipAvailable = player.getVipTier() > 0 && !vipRewards.isEmpty();
        minLevel = AttendanceSettings.minimumLevel();

        pcCafeMask = engine.getPcCafeMask();

        if(available) {
            lastReceived = (byte) (player.lastAttendanceReward() % rewards.size());
            nextReceive = (byte) (lastReceived + 1);
        } else {
            lastReceived = player.lastAttendanceReward();
            nextReceive = lastReceived;
        }
        if(nextReceive == 1) {
            vipRewardedMask = 0;
        } else {
            vipRewardedMask = player.getVipAttendanceReward();
        }

    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_VIP_ATTENDANCE_ITEM_LIST, buffer );

        buffer.writeByte(nextReceive);
        buffer.writeByte(lastReceived);
        buffer.writeInt(pcCafeMask);
        buffer.writeInt(vipRewardedMask);
        buffer.writeByte(0x00); // type ignored by client
        buffer.writeByte(available);
        buffer.writeByte(vipAvailable);

        buffer.writeByte(rewards.size());
        for (var reward : rewards) {
            buffer.writeInt(reward.id());
            buffer.writeLong(reward.count());
            buffer.writeByte(0x01); // multiple ignored by client
            buffer.writeByte(reward.highlight());
        }

        buffer.writeByte(vipRewards.size());
        for (var vipReward : vipRewards) {
            buffer.writeByte(0x01); // vip day info
            buffer.writeInt(vipReward.id());
            buffer.writeInt((int) vipReward.count()); // there is a client bug, the count shown on client will be the count of last non vip reward
            buffer.writeByte(vipReward.vipLevel());
            buffer.writeByte(!vipReward.highlight());
        }

        buffer.writeInt(minLevel);
    }

}
