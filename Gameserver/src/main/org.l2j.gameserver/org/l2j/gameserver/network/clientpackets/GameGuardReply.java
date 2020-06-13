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
package org.l2j.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Format: c dddd
 *
 * @author KenM
 */
public class GameGuardReply extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameGuardReply.class);
    private static final byte[] VALID =
            {
                    (byte) 0x88,
                    0x40,
                    0x1c,
                    (byte) 0xa7,
                    (byte) 0x83,
                    0x42,
                    (byte) 0xe9,
                    0x15,
                    (byte) 0xde,
                    (byte) 0xc3,
                    0x68,
                    (byte) 0xf6,
                    0x2d,
                    0x23,
                    (byte) 0xf1,
                    0x3f,
                    (byte) 0xee,
                    0x68,
                    0x5b,
                    (byte) 0xc5,
            };

    private final byte[] _reply = new byte[8];

    @Override
    public void readImpl() {
        readBytes(_reply, 0, 4);
        readInt();
        readBytes(_reply, 4, 4);
    }

    @Override
    public void runImpl() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            final byte[] result = md.digest(_reply);
            if (Arrays.equals(result, VALID)) {
                client.setGameGuardOk(true);
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
        }
    }
}
