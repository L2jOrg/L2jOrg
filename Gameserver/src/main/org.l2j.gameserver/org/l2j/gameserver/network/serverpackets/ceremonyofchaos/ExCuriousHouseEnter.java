package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExCuriousHouseEnter extends IClientOutgoingPacket {
    public static final ExCuriousHouseEnter STATIC_PACKET = new ExCuriousHouseEnter();

    private ExCuriousHouseEnter() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CURIOUS_HOUSE_ENTER.writeId(packet);
    }
}
