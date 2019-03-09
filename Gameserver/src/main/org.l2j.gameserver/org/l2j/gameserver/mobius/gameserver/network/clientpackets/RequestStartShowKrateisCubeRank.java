package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class RequestStartShowKrateisCubeRank extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {

    }

    @Override
    public void runImpl()
    {
        // TODO: Implement.
        System.out.println("RequestStartShowKrateisCubeRank");
    }
}
