package org.l2j.gameserver.mobius.gameserver.network.serverpackets.attendance;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExVipAttendanceItemList extends IClientOutgoingPacket {
    boolean _available;
    int _index;

    public ExVipAttendanceItemList(L2PcInstance player) {
        final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
        _available = attendanceInfo.isRewardAvailable();
        _index = attendanceInfo.getRewardIndex();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VIP_ATTENDANCE_ITEM_LIST.writeId(packet);
        packet.put((byte) (_available ? _index + 1 : _index)); // index to receive?
        packet.put((byte) _index); // last received index?
        packet.putInt(0x00);
        packet.putInt(0x00);
        packet.put((byte) 0x01);
        packet.put((byte) (_available ? 0x01 : 0x00)); // player can receive reward today?
        packet.put((byte) 250);
        packet.put((byte) AttendanceRewardData.getInstance().getRewardsCount()); // reward size
        int rewardCounter = 0;
        for (ItemHolder reward : AttendanceRewardData.getInstance().getRewards()) {
            rewardCounter++;
            packet.putInt(reward.getId());
            packet.putLong(reward.getCount());
            packet.put((byte) 0x01); // is unknown?
            packet.put((byte) ((rewardCounter % 7) == 0 ? 0x01 : 0x00)); // is last in row?
        }
        packet.put((byte) 0x00);
        packet.putInt(0x00);
    }
}
