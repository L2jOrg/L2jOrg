package org.l2j.gameserver.network.serverpackets.compound;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExEnchantOneRemoveFail extends ServerPacket {
    public static final ExEnchantOneRemoveFail STATIC_PACKET = new ExEnchantOneRemoveFail();

    private ExEnchantOneRemoveFail() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ENCHANT_ONE_REMOVE_FAIL);
    }

}
