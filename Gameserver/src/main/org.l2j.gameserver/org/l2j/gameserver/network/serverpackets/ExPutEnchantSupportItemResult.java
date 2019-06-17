package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author nBd
 */
public class ExPutEnchantSupportItemResult extends ServerPacket {
    private final int _result;

    public ExPutEnchantSupportItemResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_PUT_ENCHANT_SUPPORT_ITEM_RESULT);

        writeInt(_result);
    }

}
