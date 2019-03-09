package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

import java.nio.ByteBuffer;

/**
 * @author ShanSoft Packets Structure: chddd
 */
public final class RequestChangeBookMarkSlot extends IClientIncomingPacket
{
    public RequestChangeBookMarkSlot(L2GameClient client) {
        this.client = client;
    }

    @Override
    public void readImpl(ByteBuffer packet)
    {
    }

    @Override
    public void runImpl()
    {

    }
}
