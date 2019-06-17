package org.l2j.gameserver.network.serverpackets.adenadistribution;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_DIVIDE_ADENA_START);
    }

}