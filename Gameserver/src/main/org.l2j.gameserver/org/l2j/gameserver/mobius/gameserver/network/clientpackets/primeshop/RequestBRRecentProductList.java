package org.l2j.gameserver.mobius.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRRecentProductList extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {

    }

    @Override
    public void runImpl()
    {
        // L2PcInstance player = client.getActiveChar();
        // TODO: Implement it.
    }
}