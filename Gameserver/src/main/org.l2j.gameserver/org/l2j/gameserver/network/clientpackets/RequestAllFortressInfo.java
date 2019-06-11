package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExShowFortressInfo;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class RequestAllFortressInfo extends IClientIncomingPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        client.sendPacket(ExShowFortressInfo.STATIC_PACKET);
    }
}
