package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExCuriousHouseEnter extends ServerPacket {
    public static final ExCuriousHouseEnter STATIC_PACKET = new ExCuriousHouseEnter();

    private ExCuriousHouseEnter() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CURIOUS_HOUSE_ENTER);
    }

}
