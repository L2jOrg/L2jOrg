package org.l2j.gameserver.network.serverpackets.attendance;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExConfirmVipAttendanceCheck extends IClientOutgoingPacket {
    boolean _available;
    int _index;

    public ExConfirmVipAttendanceCheck(boolean rewardAvailable, int rewardIndex) {
        _available = rewardAvailable;
        _index = rewardIndex;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CONFIRM_VIP_ATTENDANCE_CHECK.writeId(packet);
        packet.put((byte) (_available ? 0x01 : 0x00)); // can receive reward today? 1 else 0
        packet.put((byte) _index); // active reward index
        packet.putInt(0);
        packet.putInt(0);
    }
}
