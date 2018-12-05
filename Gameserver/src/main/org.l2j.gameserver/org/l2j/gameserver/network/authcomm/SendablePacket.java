package org.l2j.gameserver.network.authcomm;

import org.l2j.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket extends WritablePacket<AuthServerClient> {
	private static final Logger logger = LoggerFactory.getLogger(SendablePacket.class);

	public boolean write() {
		try {
			writeImpl();
		} catch(Exception e) {
			logger.error("", e);
		}
		return true;
	}

	protected abstract void writeImpl();
}
