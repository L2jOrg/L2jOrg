package org.l2j.gameserver.network.clientpackets.attendance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.attendance.ExConfirmVipAttendanceCheck;
import org.l2j.gameserver.settings.AttendanceSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Mobius
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

        if (!getSettings(AttendanceSettings.class).enabled()) {
            player.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            return;
        }

        if (getSettings(AttendanceSettings.class).vipOnly() && player.getVipTier() <= 0) {
            player.sendPacket(SystemMessageId.YOUR_VIP_LEVEL_IS_TOO_LOW_TO_RECEIVE_THE_REWARD);
            return;
        }

        // Check login delay.
        var delay = getSettings(AttendanceSettings.class).delay();
        if (player.getUptime() < ( delay * 60 * 1000)) {
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT).addInt(delay).addInt((int) (delay - player.getUptime())) );
            return;
        }

        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        final boolean isRewardAvailable = attendanceInfo.isRewardAvailable();
        final int rewardIndex = attendanceInfo.getRewardIndex();
        final ItemHolder reward = AttendanceRewardData.getInstance().getRewards().get(rewardIndex);
        final ItemTemplate itemTemplate = ItemEngine.getInstance().getTemplate(reward.getId());

        // Weight check.
        final long weight = itemTemplate.getWeight() * reward.getCount();
        final long slots = itemTemplate.isStackable() ? 1 : reward.getCount();
        if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
            return;
        }

        // Reward.
        if (isRewardAvailable) {
            // Save date and index.
            player.setAttendanceInfo(rewardIndex + 1);
            // Add items to player.
            player.addItem("Attendance Reward", reward, player, true);
            // Send message.
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_RECEIVED_YOUR_VIP_ATTENDANCE_REWARD_FOR_DAY_S1);
            msg.addInt(rewardIndex + 1);
            player.sendPacket(msg);
            // Send confirm packet.
            player.sendPacket(new ExConfirmVipAttendanceCheck(isRewardAvailable, rewardIndex + 1));
        }
    }
}
