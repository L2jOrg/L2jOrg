package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author GodKratos
 */
public class ExOlympiadMatchEnd extends IClientOutgoingPacket {
    public static final ExOlympiadMatchEnd STATIC_PACKET = new ExOlympiadMatchEnd();

    private ExOlympiadMatchEnd() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_OLYMPIAD_MATCH_END.writeId(packet);
    }
}