package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class ReceivablePacket extends ReadablePacket<AuthServerClient> {
	private static final Logger logger = LoggerFactory.getLogger(ReceivablePacket.class);

	@Override
	public final boolean read(ByteBuffer buffer) {
		try {
			readImpl(buffer);
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

	protected abstract void readImpl(ByteBuffer buffer);

	protected abstract void runImpl();

	protected void sendPacket(SendablePacket sp)
	{
		client.sendPacket(sp);
	}
}
