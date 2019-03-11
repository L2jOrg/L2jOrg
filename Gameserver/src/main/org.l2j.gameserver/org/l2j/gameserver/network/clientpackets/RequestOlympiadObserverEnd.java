package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * format ch c: (id) 0xD0 h: (subid) 0x12
 *
 * @author -Wooden-
 */
public final class RequestOlympiadObserverEnd extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.inObserverMode()) {
            activeChar.leaveOlympiadObserverMode();
        }
    }
}