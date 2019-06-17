package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.MovieHolder;

/**
 * @author St3eT
 */
public final class RequestExEscapeScene extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final MovieHolder holder = activeChar.getMovieHolder();
        if (holder == null) {
            return;
        }
        holder.playerEscapeVote(activeChar);
    }
}