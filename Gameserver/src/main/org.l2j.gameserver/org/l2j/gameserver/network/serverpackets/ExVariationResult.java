package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Format: (ch)ddd
 */
public class ExVariationResult extends ServerPacket {
    private final int _option1;
    private final int _option2;
    private final int _success;

    public ExVariationResult(int option1, int option2, boolean success) {
        _option1 = option1;
        _option2 = option2;
        _success = success ? 0x01 : 0x00;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_VARIATION_RESULT);

        writeInt(_option1);
        writeInt(_option2);
        writeInt(_success);
    }

}

