package org.l2j.gameserver.mobius.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

/**
 * This Packet doesn't seem to be doing anything.
 *
 * @author NosBit
 */
public class RequestCommissionCancel extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {
    }
}
