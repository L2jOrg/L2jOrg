package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;
import io.github.joealisson.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameServerWritablePacket extends WritablePacket<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameServerWritablePacket.class);

	@Override
	protected boolean write() {
	    try {
            writeImpl();
            return  true;
        } catch (Exception e) {
	        logger.error(e.getLocalizedMessage(), e);
        }
        return false;

	}

	protected abstract void writeImpl() throws Exception ;

	@Override
	protected int packetSize() {
		return 22; // CHECKSUM + PADDING + HEADER
	}
}
