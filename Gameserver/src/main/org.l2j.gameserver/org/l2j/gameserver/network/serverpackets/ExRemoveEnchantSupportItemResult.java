package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
@StaticPacket
public class ExRemoveEnchantSupportItemResult extends IClientOutgoingPacket {
    public static final ExRemoveEnchantSupportItemResult STATIC_PACKET = new ExRemoveEnchantSupportItemResult();

    private ExRemoveEnchantSupportItemResult() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_REMOVE_ENCHANT_SUPPORT_ITEM_RESULT);

    }

}
