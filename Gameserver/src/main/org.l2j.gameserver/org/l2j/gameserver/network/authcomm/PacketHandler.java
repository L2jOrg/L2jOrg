package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.PacketBuffer;
import io.github.joealisson.mmocore.ReadablePacket;
import org.l2j.gameserver.network.authcomm.as2gs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author JoeAlisson
 */
public class PacketHandler implements io.github.joealisson.mmocore.PacketHandler<AuthServerClient> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandler.class);

	public ReadablePacket<AuthServerClient> handlePacket(PacketBuffer buf, AuthServerClient client) {
		int id = buf.read() & 0xff;
		return switch(id) {
			case 0x00 -> new AuthResponse();
			case 0x01 -> new LoginServerFail();
			case 0x02 -> new PlayerAuthResponse();
			case 0x03 -> new KickPlayer();
			case 0x04 -> new GetAccountInfo();
			case 0x06 -> null; // new ChangePasswordResponse();
			case 0xff -> new PingRequest();
			default -> {
				LOGGER.error("Received unknown packet: {}", Integer.toHexString(id));
				yield null;
			}
		};
	}
}
