package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author nBd
 */
public class ExPutEnchantTargetItemResult extends ServerPacket {
    private final int objectId;

    public ExPutEnchantTargetItemResult(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PUT_ENCHANT_TARGET_ITEM_RESULT);
        writeInt(objectId);
    }

}
