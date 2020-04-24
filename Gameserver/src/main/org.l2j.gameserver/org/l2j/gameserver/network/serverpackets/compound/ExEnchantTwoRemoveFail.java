package org.l2j.gameserver.network.serverpackets.compound;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExEnchantTwoRemoveFail extends ServerPacket {
    public static final ExEnchantTwoRemoveFail STATIC_PACKET = new ExEnchantTwoRemoveFail();

    private ExEnchantTwoRemoveFail() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ENCHANT_TWO_REMOVE_FAIL);
    }

}
