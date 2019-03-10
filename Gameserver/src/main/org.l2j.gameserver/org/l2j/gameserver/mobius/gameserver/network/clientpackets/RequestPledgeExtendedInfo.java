package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import java.nio.ByteBuffer;

/**
 * Format: (c) S S: pledge name?
 *
 * @author -Wooden-
 */
public class RequestPledgeExtendedInfo extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private String _name;

    @Override
    public void readImpl(ByteBuffer packet) {
        _name = readString(packet);
    }

    @Override
    public void runImpl() {
        // TODO: Implement
    }
}
