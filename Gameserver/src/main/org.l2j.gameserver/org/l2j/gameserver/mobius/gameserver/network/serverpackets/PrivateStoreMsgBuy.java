package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PrivateStoreMsgBuy extends IClientOutgoingPacket {
    private final int _objId;
    private String _storeMsg;

    public PrivateStoreMsgBuy(L2PcInstance player) {
        _objId = player.getObjectId();
        if (player.getBuyList() != null) {
            _storeMsg = player.getBuyList().getTitle();
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PRIVATE_STORE_BUY_MSG.writeId(packet);

        packet.putInt(_objId);
        writeString(_storeMsg, packet);
    }
}
