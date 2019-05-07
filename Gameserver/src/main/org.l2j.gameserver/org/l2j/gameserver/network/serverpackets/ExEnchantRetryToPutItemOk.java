package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExEnchantRetryToPutItemOk extends IClientOutgoingPacket {
    public static final ExEnchantRetryToPutItemOk STATIC_PACKET = new ExEnchantRetryToPutItemOk();

    private ExEnchantRetryToPutItemOk() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_RETRY_TO_PUT_ITEM_OK.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 3;
    }
}