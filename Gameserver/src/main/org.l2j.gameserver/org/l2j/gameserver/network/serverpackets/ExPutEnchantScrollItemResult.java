package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExPutEnchantScrollItemResult extends IClientOutgoingPacket {
    private final int _result;

    public ExPutEnchantScrollItemResult(int result) {
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PUT_ENCHANT_SCROLL_ITEM_RESULT.writeId(packet);

        packet.putInt(_result);
    }
}