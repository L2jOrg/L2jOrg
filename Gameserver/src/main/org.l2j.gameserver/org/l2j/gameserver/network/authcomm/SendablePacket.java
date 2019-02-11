package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class SendablePacket extends WritablePacket<AuthServerClient> {
	private static final Logger logger = LoggerFactory.getLogger(SendablePacket.class);

	@Override
	public boolean write(AuthServerClient client, ByteBuffer buffer) {
		try {
			writeImpl(client, buffer);
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	protected abstract void writeImpl(AuthServerClient client, ByteBuffer buffer);
}
