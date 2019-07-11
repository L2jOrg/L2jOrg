package org.l2j.gameserver.network.clientpackets.attendance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.attendance.ExConfirmVipAttendanceCheck;

/**
 * @author Mobius
 */
public class RequestVipAttendanceCheck extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!Config.ENABLE_ATTENDANCE_REWARDS) {
            activeChar.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            return;
        }

        if (Config.VIP_ONLY_ATTENDANCE_REWARDS && activeChar.getVipTier() <= 0) {
            activeChar.sendPacket(SystemMessageId.YOUR_VIP_RANK_IS_TOO_LOW_TO_RECEIVE_THE_REWARD);
            return;
        }

        // Check login delay.
        if (activeChar.getUptime() < (Config.ATTENDANCE_REWARD_DELAY * 60 * 1000)) {
            // activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_LEVEL_REQUIREMENTS_TO_RECEIVE_THE_ATTENDANCE_REWARD_PLEASE_CHECK_THE_REQUIRED_LEVEL_YOU_CAN_REDEEM_YOUR_REWARD_30_MINUTES_AFTER_LOGGING_IN);
            activeChar.sendMessage("You can redeem your reward " + Config.ATTENDANCE_REWARD_DELAY + " minutes after logging in.");
            return;
        }

        final AttendanceInfoHolder attendanceInfo = activeChar.getAttendanceInfo();
        final boolean isRewardAvailable = attendanceInfo.isRewardAvailable();
        final int rewardIndex = attendanceInfo.getRewardIndex();
        final ItemHolder reward = AttendanceRewardData.getInstance().getRewards().get(rewardIndex);
        final ItemTemplate itemTemplate = ItemTable.getInstance().getTemplate(reward.getId());

        // Weight check.
        final long weight = itemTemplate.getWeight() * reward.getCount();
        final long slots = itemTemplate.isStackable() ? 1 : reward.getCount();
        if (!activeChar.getInventory().validateWeight(weight) || !activeChar.getInventory().validateCapacity(slots)) {
            activeChar.sendPacket(SystemMessageId.THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
            return;
        }

        // Reward.
        if (isRewardAvailable) {
            // Save date and index.
            activeChar.setAttendanceInfo(rewardIndex + 1);
            // Add items to player.
            activeChar.addItem("Attendance Reward", reward, activeChar, true);
            // Send message.
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_VE_RECEIVED_YOUR_VIP_ATTENDANCE_REWARD_FOR_DAY_S1);
            msg.addInt(rewardIndex + 1);
            activeChar.sendPacket(msg);
            // Send confirm packet.
            activeChar.sendPacket(new ExConfirmVipAttendanceCheck(isRewardAvailable, rewardIndex + 1));
        }
    }
}
