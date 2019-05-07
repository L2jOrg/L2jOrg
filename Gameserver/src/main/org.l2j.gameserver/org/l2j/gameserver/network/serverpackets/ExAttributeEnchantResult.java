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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ATTRIBUTE_ENCHANT_RESULT.writeId(packet);

        packet.putInt(_result);
        packet.put((byte) _isWeapon);
        packet.putShort((short) _type);
        packet.putShort((short) _before);
        packet.putShort((short) _after);
        packet.putShort((short) _successCount);
        packet.putShort((short) _failedCount);
    }

    @Override
    protected int size(L2GameClient client) {
        return 20;
    }
}
