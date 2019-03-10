package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.MovieHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public final class EndScenePlayer extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndScenePlayer.class);
    private int _movieId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _movieId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (_movieId == 0)) {
            return;
        }

        final MovieHolder holder = activeChar.getMovieHolder();
        if ((holder == null) || (holder.getMovie().getClientId() != _movieId)) {
            LOGGER.warn("Player " + client + " sent EndScenePlayer with wrong movie id: " + _movieId);
            return;
        }
        activeChar.stopMovie();
    }
}