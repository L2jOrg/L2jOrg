package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PrivateStoreMsgSell extends ServerPacket {
    private final int _objId;
    private String _storeMsg;

    public PrivateStoreMsgSell(Player player) {
        _objId = player.getObjectId();
        if ((player.getSellList() != null) || player.isSellingBuffs()) {
            _storeMsg = player.getSellList().getTitle();
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PRIVATE_STORE_MSG);

        writeInt(_objId);
        writeString(_storeMsg);
    }

}
