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
public class ExPutShapeShiftingTargetItemResult extends IClientOutgoingPacket {
    public static int RESULT_FAILED = 0x00;
    public static int RESULT_SUCCESS = 0x01;

    public static ExPutShapeShiftingTargetItemResult FAILED = new ExPutShapeShiftingTargetItemResult(RESULT_FAILED, 0);

    private final int _resultId;
    private final long _price;

    public ExPutShapeShiftingTargetItemResult(int resultId, long price) {
        _resultId = resultId;
        _price = price;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PUT_SHAPE_SHIFTING_TARGET_ITEM_RESULT.writeId(packet);

        packet.putInt(_resultId);
        packet.putLong(_price);
    }
}