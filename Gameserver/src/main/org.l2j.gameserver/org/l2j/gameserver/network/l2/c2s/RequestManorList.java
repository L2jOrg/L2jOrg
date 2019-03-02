package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.ExSendManorListPacket;

import java.nio.ByteBuffer;

public class RequestManorList extends  L2GameClientPacket{

    @Override
    protected void readImpl(ByteBuffer buffer) {
        // Trigger Packet
    }

    @Override
    protected void runImpl() throws Exception {
        sendPacket(ExSendManorListPacket.STATIC_PACKET);
    }
}
