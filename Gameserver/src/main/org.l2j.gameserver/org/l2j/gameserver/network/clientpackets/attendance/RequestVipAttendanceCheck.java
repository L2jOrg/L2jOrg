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
package org.l2j.gameserver.network.clientpackets.attendance;

import org.l2j.gameserver.engine.item.AttendanceEngine;
import org.l2j.gameserver.engine.item.AttendanceItem;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.attendance.ExConfirmVipAttendanceCheck;
import org.l2j.gameserver.settings.AttendanceSettings;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.YOU_DO_NOT_MEET_THE_LEVEL_REQUIREMENTS_TO_RECEIVE_THE_ATTENDANCE_REWARD_PLEASE_CHECK_THE_REQUIRED_LEVEL_YOU_CAN_REDEEM_YOUR_REWARD_30_MINUTES_AFTER_LOGGING_IN;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class RequestVipAttendanceCheck extends ClientPacket {

    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!AttendanceSettings.enabled()) {
            player.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            return;
        }

        if (AttendanceSettings.vipOnly() && player.getVipTier() <= 0) {
            player.sendPacket(SystemMessageId.YOUR_VIP_LEVEL_IS_TOO_LOW_TO_RECEIVE_THE_REWARD);
            return;
        }

        if(player.getLevel() < AttendanceSettings.minimumLevel()) {
            player.sendPacket(YOU_DO_NOT_MEET_THE_LEVEL_REQUIREMENTS_TO_RECEIVE_THE_ATTENDANCE_REWARD_PLEASE_CHECK_THE_REQUIRED_LEVEL_YOU_CAN_REDEEM_YOUR_REWARD_30_MINUTES_AFTER_LOGGING_IN);
            return;
        }

        var delay = AttendanceSettings.delay();
        var uptimeInMinutes = player.getUptime() / 60000;
        if (uptimeInMinutes < delay) {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT).addInt(delay).addInt((int) (delay - uptimeInMinutes)) );
            return;
        }

        final var attendanceEngine = AttendanceEngine.getInstance();
        final var rewards = attendanceEngine.getRewards();

        if (player.canReceiveAttendance() && !rewards.isEmpty()) {
            byte rewardIndex = (byte) (player.lastAttendanceReward() % rewards.size());

            final var reward = rewards.get(rewardIndex);

            final var itemTemplate = ItemEngine.getInstance().getTemplate(reward.id());
            long weight = itemTemplate.getWeight() * reward.count();
            long slots = itemTemplate.isStackable() ? 1 : reward.count();

            AttendanceItem vipReward = getVipReward(player, attendanceEngine, rewardIndex);

            if(nonNull(vipReward)) {
                final var vipItemTemplate = ItemEngine.getInstance().getTemplate(vipReward.id());
                weight += vipItemTemplate.getWeight() * vipReward.count();
                slots += vipItemTemplate.isStackable() ? 1 : vipReward.count();
            }

            if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots)) {
                player.sendPacket(SystemMessageId.THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
                return;
            }

            rewardIndex++;
            player.updateAttendance(rewardIndex, nonNull(vipReward));
            player.addItem("Attendance Reward", reward.id(), reward.count(), player, true);

            if(nonNull(vipReward)) {
                player.addItem("Attendance Reward", vipReward.id(), vipReward.count(), player, true);
            }

            if(reward.cafePoints() > 0 ) {
                player.setPcCafePoints(player.getPcCafePoints() + reward.cafePoints());
            }

            final var msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_RECEIVED_YOUR_VIP_ATTENDANCE_REWARD_FOR_DAY_S1).addInt(rewardIndex);
            player.sendPackets(msg, new ExConfirmVipAttendanceCheck(true, rewardIndex, reward.cafePoints() > 0, nonNull(vipReward)));
        }
    }

    private AttendanceItem getVipReward(Player player, AttendanceEngine attendanceEngine, byte rewardIndex) {
        final var vipRewards = attendanceEngine.getVipRewards();
        AttendanceItem vipReward = null;
        if((rewardIndex + 1) % 7 == 0 && !vipRewards.isEmpty()) {
            int vipRewardIndex =  (rewardIndex / 7) %  vipRewards.size();
            vipReward = vipRewards.get(vipRewardIndex);

            if(vipReward.vipLevel() > player.getVipTier()) {
                vipReward = null;
            }
        }
        return vipReward;
    }
}
