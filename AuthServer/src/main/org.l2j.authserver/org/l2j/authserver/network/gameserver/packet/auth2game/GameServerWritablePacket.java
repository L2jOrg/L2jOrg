package org.l2j.authserver.network.gameserver.packet.auth2game;

import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class GameServerWritablePacket extends WritablePacket<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameServerWritablePacket.class);

	@Override
	protected boolean write(ServerClient client, ByteBuffer buffer) {
	    try {
            writeImpl(client, buffer);
            return  true;
        } catch (Exception e) {
	        logger.error(e.getLocalizedMessage(), e);
        }
        return false;

	}

	protected abstract void writeImpl(ServerClient client, ByteBuffer buffer) throws Exception ;

	@Override
	protected int size(ServerClient client) {
		return 22; // CHECKSUM + PADDING + HEADER
	}
}
