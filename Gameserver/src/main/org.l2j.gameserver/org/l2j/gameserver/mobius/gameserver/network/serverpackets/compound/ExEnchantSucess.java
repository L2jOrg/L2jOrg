package org.l2j.gameserver.mobius.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

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
}
