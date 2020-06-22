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

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.AuthServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fromat: d d: response
 */
public final class GGAuth extends AuthServerPacket {
    private static final Logger logger = LoggerFactory.getLogger(GGAuth.class);
    public static final int SKIP_GG_AUTH_REQUEST = 0x0b;

    private final int _response;

    public GGAuth(int response) {
        _response = response;
        logger.debug("Reason Hex: {}", Integer.toHexString(response));
    }

    @Override
    protected void writeImpl(AuthClient client) {
        writeByte(0x0b);
        writeInt(_response);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00);
    }

}
