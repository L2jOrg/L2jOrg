package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PrivateStoreMsgSell extends IClientOutgoingPacket {
    private final int _objId;
    private String _storeMsg;

    public PrivateStoreMsgSell(L2PcInstance player) {
        _objId = player.getObjectId();
        if ((player.getSellList() != null) || player.isSellingBuffs()) {
            _storeMsg = player.getSellList().getTitle();
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PRIVATE_STORE_MSG.writeId(packet);

        packet.putInt(_objId);
        writeString(_storeMsg, packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 11 +_storeMsg.length() * 2;
    }
}
