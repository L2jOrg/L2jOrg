package org.l2j.authserver.network.client.packet;

import org.l2j.authserver.network.client.AuthClient;
import io.github.joealisson.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public abstract class L2LoginClientPacket extends ReadablePacket<AuthClient> {

	private static Logger _log = LoggerFactory.getLogger(L2LoginClientPacket.class.getName());
	
	@Override
	protected final boolean read(ByteBuffer buffer) {
		try {
			return readImpl(buffer);
		} catch (Exception e) {
			_log.error("ERROR READING: " + this.getClass().getSimpleName());
			e.printStackTrace();
			return false;
		}
	}
	
	protected abstract boolean readImpl(ByteBuffer buffer);
}
