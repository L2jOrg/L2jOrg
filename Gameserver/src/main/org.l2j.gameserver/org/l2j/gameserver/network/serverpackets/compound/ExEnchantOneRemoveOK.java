package org.l2j.gameserver.network.serverpackets.compound;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExEnchantOneRemoveOK extends IClientOutgoingPacket {
    public static final ExEnchantOneRemoveOK STATIC_PACKET = new ExEnchantOneRemoveOK();

    private ExEnchantOneRemoveOK() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENCHANT_ONE_REMOVE_OK.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}
