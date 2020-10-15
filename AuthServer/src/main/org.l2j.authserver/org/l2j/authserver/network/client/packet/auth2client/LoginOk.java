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
package org.l2j.authserver.network.client.packet.auth2client;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.AuthServerPacket;

public final class LoginOk extends AuthServerPacket {

	@Override
	protected void writeImpl(AuthClient client, WritableBuffer buffer) {
		var sessionKey = client.getSessionKey();
		buffer.writeByte(0x03);
		buffer.writeInt(sessionKey.getAuthAccountId());
		buffer.writeInt(sessionKey.getAuthKey());
		buffer.writeBytes(new byte[8]);
		buffer.writeInt(0x000003ea); // billing type: 1002 Free, x200 paid time, x500 flat rate pre paid, others subscription
		buffer.writeInt(0x00); // paid time
		buffer.writeInt(0x00);
		buffer.writeInt(0x00); // warning mask
		buffer.writeBytes(new byte[16]); // forbidden servers
		buffer.writeInt(0x00);
	}

}
