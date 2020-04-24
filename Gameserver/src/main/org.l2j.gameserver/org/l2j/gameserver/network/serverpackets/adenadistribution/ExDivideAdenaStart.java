package org.l2j.gameserver.network.serverpackets.adenadistribution;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
@StaticPacket
public class ExDivideAdenaStart extends ServerPacket {
    public static final ExDivideAdenaStart STATIC_PACKET = new ExDivideAdenaStart();

    private ExDivideAdenaStart() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DIVIDE_ADENA_START);
    }

}