package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PrivateStoreMsgBuy extends ServerPacket {
    private final int _objId;
    private String _storeMsg;

    public PrivateStoreMsgBuy(Player player) {
        _objId = player.getObjectId();
        if (player.getBuyList() != null) {
            _storeMsg = player.getBuyList().getTitle();
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PRIVATE_STORE_BUY_MSG);

        writeInt(_objId);
        writeString(_storeMsg);
    }

}
