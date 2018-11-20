package org.l2j.authserver.network.client.packet;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public abstract class L2LoginClientPacket extends ReadablePacket<AuthClient> {

	private static Logger _log = LoggerFactory.getLogger(L2LoginClientPacket.class.getName());
	
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
