package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExSendManorList;

import java.nio.ByteBuffer;

/**
 * @author l3x
 */
public class RequestManorList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        client.sendPacket(ExSendManorList.STATIC_PACKET);
    }
}