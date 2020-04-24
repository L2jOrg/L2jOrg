package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * (just a trigger)
 *
 * @author -Wooden-
 */
@StaticPacket
public class ExMailArrived extends ServerPacket {
    public static final ExMailArrived STATIC_PACKET = new ExMailArrived();

    private ExMailArrived() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_MAIL_ARRIVED);
    }

}
