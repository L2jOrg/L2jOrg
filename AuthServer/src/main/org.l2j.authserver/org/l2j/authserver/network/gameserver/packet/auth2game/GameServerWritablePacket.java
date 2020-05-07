package org.l2j.authserver.network.gameserver.packet.auth2game;

import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.authserver.network.gameserver.ServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameServerWritablePacket extends WritablePacket<ServerClient> {

    private static final Logger logger = LoggerFactory.getLogger(GameServerWritablePacket.class);

	@Override
	protected boolean write(ServerClient client) {
	    try {
            writeImpl(client);
            return  true;
        } catch (Exception e) {
	        logger.error(e.getMessage(), e);
        }
        return false;

	}

	protected abstract void writeImpl(ServerClient client) throws Exception ;

}
