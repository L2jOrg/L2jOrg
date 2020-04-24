package org.l2j.gameserver.network.serverpackets.attendance;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExConfirmVipAttendanceCheck extends ServerPacket {
    boolean _available;
    int _index;

    public ExConfirmVipAttendanceCheck(boolean rewardAvailable, int rewardIndex) {
        _available = rewardAvailable;
        _index = rewardIndex;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CONFIRM_VIP_ATTENDANCE_CHECK);
        writeByte((byte) (_available ? 0x01 : 0x00)); // can receive reward today? 1 else 0
        writeByte((byte) _index); // active reward index
        writeInt(0);
        writeInt(0);
    }

}
