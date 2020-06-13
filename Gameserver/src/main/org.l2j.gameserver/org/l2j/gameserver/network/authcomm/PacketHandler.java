/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
