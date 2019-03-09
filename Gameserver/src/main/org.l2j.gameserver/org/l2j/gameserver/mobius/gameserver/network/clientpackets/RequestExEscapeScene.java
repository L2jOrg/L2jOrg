package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.MovieHolder;

import java.nio.ByteBuffer;

/**
 * @author St3eT
 */
public final class RequestExEscapeScene extends IClientIncomingPacket
{
    @Override
    public void readImpl(ByteBuffer packet)
    {
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        final MovieHolder holder = activeChar.getMovieHolder();
        if (holder == null)
        {
            return;
        }
        holder.playerEscapeVote(activeChar);
    }
}