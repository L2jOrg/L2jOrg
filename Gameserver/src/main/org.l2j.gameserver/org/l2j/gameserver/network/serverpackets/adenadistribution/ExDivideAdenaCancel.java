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
public class ExDivideAdenaCancel extends IClientOutgoingPacket {
    public static final ExDivideAdenaCancel STATIC_PACKET = new ExDivideAdenaCancel();

    private ExDivideAdenaCancel() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_DIVIDE_ADENA_CANCEL.writeId(packet);

        packet.put((byte) 0x00); // TODO: Find me
    }
}
