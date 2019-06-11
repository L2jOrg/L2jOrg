package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExAttributeEnchantResult extends IClientOutgoingPacket {
    private final int _result;
    private final int _isWeapon;
    private final int _type;
    private final int _before;
    private final int _after;
    private final int _successCount;
    private final int _failedCount;

    public ExAttributeEnchantResult(int result, boolean isWeapon, AttributeType type, int before, int after, int successCount, int failedCount) {
        _result = result;
        _isWeapon = isWeapon ? 1 : 0;
        _type = type.getClientId();
        _before = before;
        _after = after;
        _successCount = successCount;
        _failedCount = failedCount;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_ATTRIBUTE_ENCHANT_RESULT);

        writeInt(_result);
        writeByte((byte) _isWeapon);
        writeShort((short) _type);
        writeShort((short) _before);
        writeShort((short) _after);
        writeShort((short) _successCount);
        writeShort((short) _failedCount);
    }

}
