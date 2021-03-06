/*
 * Copyright © 2019-2021 L2JOrg
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

/**
 * Format: dd b dddd s
 * d: session id
 * d: protocol revision
 * b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key
 * 0x10 bytes at 0x00
 * d: unknow
 * d: unknow
 * d: unknow
 * d: unknow
 * s: blowfish key
 */
public final class Init extends AuthServerPacket {

    @Override
    protected void writeImpl(AuthClient client, WritableBuffer buffer) {
        buffer.writeByte(0x00);

        buffer.writeInt(client.getSessionId());
        buffer.writeInt(0xc621);

        buffer.writeBytes(client.getScrambledModulus());

        // unk GG related?
        buffer.writeInt(0x29DD954E);
        buffer.writeInt(0x77C39CFC);
        buffer.writeInt(0x97ADB620);
        buffer.writeInt(0x07BDE0F7);

        buffer.writeBytes(client.getBlowfishKey());
        buffer.writeInt(0x00);
    }

}
