package org.l2j.authserver.network.client.packet;

import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.authserver.network.client.AuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public abstract class AuthClientPacket extends ReadablePacket<AuthClient> {

	private static Logger _log = LoggerFactory.getLogger(AuthClientPacket.class.getName());
	
	@Override
	protected final boolean read() {
		try {
			return readImpl();
		} catch (Exception e) {
			_log.error("ERROR READING: " + this.getClass().getSimpleName());
			e.printStackTrace();
			return false;
		}
	}
	
	protected abstract boolean readImpl();
}
