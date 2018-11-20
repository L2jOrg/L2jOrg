package org.l2j.authserver.network.gameserver.packet.auth2game;

import org.l2j.authserver.network.gameserver.ServerClient;
import org.l2j.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameServerWritablePacket extends WritablePacket<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameServerWritablePacket.class);

	@Override
	protected void write() {
	    try {
            writeImpl();
        } catch (Exception e) {
	        logger.error("Writing {} : {}", getClass().getSimpleName(), e);
        }

	}

	protected abstract void writeImpl() throws Exception ;

	@Override
	protected int packetSize() {
		return 22; // CHECKSUM + PADDING + HEADER
	}
}
