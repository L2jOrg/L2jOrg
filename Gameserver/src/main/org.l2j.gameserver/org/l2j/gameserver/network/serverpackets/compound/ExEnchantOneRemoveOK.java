package org.l2j.gameserver.network.serverpackets.compound;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExEnchantOneRemoveOK extends ServerPacket {
    public static final ExEnchantOneRemoveOK STATIC_PACKET = new ExEnchantOneRemoveOK();

    private ExEnchantOneRemoveOK() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_ONE_REMOVE_OK);
    }

}
