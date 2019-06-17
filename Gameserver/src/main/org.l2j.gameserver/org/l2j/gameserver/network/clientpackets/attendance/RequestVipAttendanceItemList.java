package org.l2j.gameserver.network.clientpackets.attendance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;

/**
 * @author Mobius
 */
public class RequestVipAttendanceItemList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!Config.ENABLE_ATTENDANCE_REWARDS) {
            activeChar.sendPacket(SystemMessageId.DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK);
            return;
        }

        activeChar.sendPacket(new ExVipAttendanceItemList(activeChar));
    }
}
