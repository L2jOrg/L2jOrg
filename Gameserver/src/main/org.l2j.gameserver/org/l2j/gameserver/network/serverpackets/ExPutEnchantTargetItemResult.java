package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author nBd
 */
public class ExPutEnchantTargetItemResult extends ServerPacket {
    private final int _result;

    public ExPutEnchantTargetItemResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PUT_ENCHANT_TARGET_ITEM_RESULT);

        writeInt(_result);
    }

}
