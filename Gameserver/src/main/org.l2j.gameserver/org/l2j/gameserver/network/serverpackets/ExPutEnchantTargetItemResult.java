package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author nBd
 */
public class ExPutEnchantTargetItemResult extends IClientOutgoingPacket {
    private final int _result;

    public ExPutEnchantTargetItemResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PUT_ENCHANT_TARGET_ITEM_RESULT);

        writeInt(_result);
    }

}
