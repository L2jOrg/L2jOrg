package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * Format: (ch)ddd
 */
public class ExVariationResult extends IClientOutgoingPacket {
    private final int _option1;
    private final int _option2;
    private final int _success;

    public ExVariationResult(int option1, int option2, boolean success) {
        _option1 = option1;
        _option2 = option2;
        _success = success ? 0x01 : 0x00;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_VARIATION_RESULT.writeId(packet);

        packet.putInt(_option1);
        packet.putInt(_option2);
        packet.putInt(_success);
    }

    @Override
    protected int size(L2GameClient client) {
        return 17;
    }
}

