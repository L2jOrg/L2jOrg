package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket extends ReadablePacket<AuthServerClient> {
	private static final Logger logger = LoggerFactory.getLogger(ReceivablePacket.class);

	@Override
	public final boolean read() {
		try {
			readImpl();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public final void run() {
		try {
			runImpl();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	protected abstract void readImpl();

	protected abstract void runImpl();

	protected void sendPacket(SendablePacket sp)
	{
		client.sendPacket(sp);
	}
}
