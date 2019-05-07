package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExEnchantSucess extends IClientOutgoingPacket {
    private final int _itemId;

    public ExEnchantSucess(int itemId) {
        _itemId = itemId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_SUCESS.writeId(packet);

        packet.putInt(_itemId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
