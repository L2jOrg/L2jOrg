package org.l2j.gameserver.network.serverpackets.compound;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExEnchantTwoRemoveOK extends ServerPacket {
    public static final ExEnchantTwoRemoveOK STATIC_PACKET = new ExEnchantTwoRemoveOK();

    private ExEnchantTwoRemoveOK() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_TWO_REMOVE_OK);
    }

}
