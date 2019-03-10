package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
@StaticPacket
public class ExEnchantRetryToPutItemFail extends IClientOutgoingPacket {
    public static final ExEnchantRetryToPutItemFail STATIC_PACKET = new ExEnchantRetryToPutItemFail();

    private ExEnchantRetryToPutItemFail() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_RETRY_TO_PUT_ITEM_FAIL.writeId(packet);
    }
}