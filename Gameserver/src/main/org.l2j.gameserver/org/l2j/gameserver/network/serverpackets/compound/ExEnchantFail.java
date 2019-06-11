package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExEnchantFail extends IClientOutgoingPacket {
    public static final ExEnchantFail STATIC_PACKET = new ExEnchantFail(0, 0);
    private final int _itemOne;
    private final int _itemTwo;

    public ExEnchantFail(int itemOne, int itemTwo) {
        _itemOne = itemOne;
        _itemTwo = itemTwo;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_ENCHANT_FAIL);

        writeInt(_itemOne);
        writeInt(_itemTwo);
    }

}
