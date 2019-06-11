package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author nBd
 */
public class ExPutEnchantSupportItemResult extends IClientOutgoingPacket {
    private final int _result;

    public ExPutEnchantSupportItemResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PUT_ENCHANT_SUPPORT_ITEM_RESULT);

        writeInt(_result);
    }

}
