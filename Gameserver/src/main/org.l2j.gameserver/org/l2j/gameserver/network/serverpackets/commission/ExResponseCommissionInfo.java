package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class ExResponseCommissionInfo extends IClientOutgoingPacket {
    public static final ExResponseCommissionInfo EMPTY = new ExResponseCommissionInfo();

    private final int _result;
    private final int _itemId;
    private final long _presetPricePerUnit;
    private final long _presetAmount;
    private final int _presetDurationType;

    private ExResponseCommissionInfo() {
        _result = 0;
        _itemId = 0;
        _presetPricePerUnit = 0;
        _presetAmount = 0;
        _presetDurationType = -1;
    }

    public ExResponseCommissionInfo(int itemId, long presetPricePerUnit, long presetAmount, int presetDurationType) {
        _result = 1;
        _itemId = itemId;
        _presetPricePerUnit = presetPricePerUnit;
        _presetAmount = presetAmount;
        _presetDurationType = presetDurationType;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_RESPONSE_COMMISSION_INFO);

        writeInt(_result);
        writeInt(_itemId);
        writeLong(_presetPricePerUnit);
        writeLong(_presetAmount);
        writeInt(_presetDurationType);
    }

}
