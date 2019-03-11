package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExShowSeedMapInfo;

import java.nio.ByteBuffer;

/**
 * RequestSeedPhase client packet
 */
public class RequestSeedPhase extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.sendPacket(ExShowSeedMapInfo.STATIC_PACKET);
    }
}
