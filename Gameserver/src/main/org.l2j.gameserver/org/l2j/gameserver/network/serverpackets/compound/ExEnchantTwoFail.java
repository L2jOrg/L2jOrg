package org.l2j.gameserver.network.serverpackets.compound;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExEnchantTwoFail extends ServerPacket {
    public static final ExEnchantTwoFail STATIC_PACKET = new ExEnchantTwoFail();

    private ExEnchantTwoFail() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_TWO_FAIL);
    }

}
