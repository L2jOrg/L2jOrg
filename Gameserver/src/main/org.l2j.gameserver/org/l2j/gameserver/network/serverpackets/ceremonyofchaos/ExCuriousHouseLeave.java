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
public class ExCuriousHouseLeave extends IClientOutgoingPacket {
    public static final ExCuriousHouseLeave STATIC_PACKET = new ExCuriousHouseLeave();

    private ExCuriousHouseLeave() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CURIOUS_HOUSE_LEAVE);
    }

}
