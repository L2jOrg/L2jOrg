package org.l2j.gameserver.network.serverpackets.adenadistribution;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
@StaticPacket
public class ExDivideAdenaStart extends IClientOutgoingPacket {
    public static final ExDivideAdenaStart STATIC_PACKET = new ExDivideAdenaStart();

    private ExDivideAdenaStart() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_DIVIDE_ADENA_START.writeId(packet);
    }
}