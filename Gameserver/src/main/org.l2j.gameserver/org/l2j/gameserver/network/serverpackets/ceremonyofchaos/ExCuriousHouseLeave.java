package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExCuriousHouseLeave extends ServerPacket {
    public static final ExCuriousHouseLeave STATIC_PACKET = new ExCuriousHouseLeave();

    private ExCuriousHouseLeave() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CURIOUS_HOUSE_LEAVE);
    }

}
