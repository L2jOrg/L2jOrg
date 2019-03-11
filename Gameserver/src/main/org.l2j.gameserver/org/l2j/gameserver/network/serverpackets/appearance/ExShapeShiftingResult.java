package org.l2j.gameserver.network.serverpackets.appearance;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExShapeShiftingResult extends IClientOutgoingPacket {
    public static int RESULT_FAILED = 0x00;
    public static int RESULT_SUCCESS = 0x01;
    public static int RESULT_CLOSE = 0x02;

    public static ExShapeShiftingResult FAILED = new ExShapeShiftingResult(RESULT_FAILED, 0, 0);
    public static ExShapeShiftingResult CLOSE = new ExShapeShiftingResult(RESULT_CLOSE, 0, 0);

    private final int _result;
    private final int _targetItemId;
    private final int _extractItemId;

    public ExShapeShiftingResult(int result, int targetItemId, int extractItemId) {
        _result = result;
        _targetItemId = targetItemId;
        _extractItemId = extractItemId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHAPE_SHIFTING_RESULT.writeId(packet);

        packet.putInt(_result);
        packet.putInt(_targetItemId);
        packet.putInt(_extractItemId);
    }
}
