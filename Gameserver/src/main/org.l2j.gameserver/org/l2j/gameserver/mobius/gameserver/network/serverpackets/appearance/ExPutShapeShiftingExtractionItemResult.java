package org.l2j.gameserver.mobius.gameserver.network.serverpackets.appearance;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExPutShapeShiftingExtractionItemResult extends IClientOutgoingPacket {
    public static ExPutShapeShiftingExtractionItemResult FAILED = new ExPutShapeShiftingExtractionItemResult(0x00);
    public static ExPutShapeShiftingExtractionItemResult SUCCESS = new ExPutShapeShiftingExtractionItemResult(0x01);

    private final int _result;

    private ExPutShapeShiftingExtractionItemResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PUT_SHAPE_SHIFTING_EXTRACTION_ITEM_RESULT.writeId(packet);

        packet.putInt(_result);
    }
}