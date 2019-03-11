package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExShowCastleInfo;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class RequestAllCastleInfo extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        client.sendPacket(ExShowCastleInfo.STATIC_PACKET);
    }
}
