package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CURIOUS_HOUSE_LEAVE);
    }

}
