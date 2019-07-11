package org.l2j.gameserver.network.serverpackets.attendance;

import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_VIP_ATTENDANCE_ITEM_LIST);
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
