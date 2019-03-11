package org.l2j.gameserver.network.clientpackets;

import java.nio.ByteBuffer;

/**
 * Format: (c) ddd d: dx d: dy d: dz
 *
 * @author -Wooden-
 */
public class MoveWithDelta extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private int _dx;
    @SuppressWarnings("unused")
    private int _dy;
    @SuppressWarnings("unused")
    private int _dz;

    @Override
    public void readImpl(ByteBuffer packet) {
        _dx = packet.getInt();
        _dy = packet.getInt();
        _dz = packet.getInt();
    }

    @Override
    public void runImpl() {
        // TODO this
    }
}
