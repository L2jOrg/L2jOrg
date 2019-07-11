package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
@StaticPacket
public class ExRemoveEnchantSupportItemResult extends ServerPacket {
    public static final ExRemoveEnchantSupportItemResult STATIC_PACKET = new ExRemoveEnchantSupportItemResult();

    private ExRemoveEnchantSupportItemResult() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_REMOVE_ENCHANT_SUPPORT_ITEM_RESULT);

    }

}
