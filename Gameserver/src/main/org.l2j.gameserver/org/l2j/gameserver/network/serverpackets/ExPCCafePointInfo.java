package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 * @author UnAfraid
 */
public class ExPCCafePointInfo extends IClientOutgoingPacket {
    private final int _points;
    private final int _mAddPoint;
    private final int _mPeriodType;
    private final int _remainTime;
    private final int _pointType;
    private final int _time;

    public ExPCCafePointInfo() {
        _points = 0;
        _mAddPoint = 0;
        _remainTime = 0;
        _mPeriodType = 0;
        _pointType = 0;
        _time = 0;
    }

    public ExPCCafePointInfo(int points, int pointsToAdd, int time) {
        _points = points;
        _mAddPoint = pointsToAdd;
        _mPeriodType = 1;
        _remainTime = 42; // No idea why but retail sends 42..
        _pointType = pointsToAdd < 0 ? 3 : 0; // When using points is 3
        _time = time;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PCCAFE_POINT_INFO.writeId(packet);

        packet.putInt(_points); // num points
        packet.putInt(_mAddPoint); // points inc display
        packet.put((byte) _mPeriodType); // period(0=don't show window,1=acquisition,2=use points)
        packet.putInt(_remainTime); // period hours left
        packet.put((byte) _pointType); // points inc display color(0=yellow, 1=cyan-blue, 2=red, all other black)
        packet.putInt(_time * 3); // value is in seconds * 3
    }
}
