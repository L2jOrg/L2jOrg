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
package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.client.packet.AuthClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;

import static org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason.REASON_SYSTEM_ERROR;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends AuthClientPacket {
    private static final Logger logger = LoggerFactory.getLogger(RequestAuthLogin.class);
    private final byte[] userData = new byte[128];
    private final byte[] authData = new byte[128];
    private boolean useNewAuth;

    @Override
    public boolean readImpl() {
        if (available() >= 256) {
            useNewAuth = true;
            readBytes(userData);
            readBytes(authData);
            return true;
        }

        if(available() >= 128) {
            readBytes(userData);
            readInt(); // sessionId
            readInt(); // GG
            readInt(); // GG
            readInt(); // GG
            readInt(); // GG
            readInt(); // Game Id ?
            readShort();
            readByte();
            byte[] unk = new byte[16];
            readBytes(unk);
            return true;
        }

        return false;
    }

    @Override
    public void run() {
        byte[] decUserData;
        byte[] decAuthData = null;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
            decUserData = rsaCipher.doFinal(userData, 0x00, 0x80);

            if(useNewAuth) {
                decAuthData =  rsaCipher.doFinal(authData, 0x00, 0x80);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            client.close(REASON_SYSTEM_ERROR);
            return;
        }

        String user;
        String password;
        if(useNewAuth) {
            user = new String(decUserData, 0x4E, 0x32).trim().toLowerCase() + new String(decAuthData, 0x4E, 0xE).trim().toLowerCase();
            password = new String(decAuthData, 0x5C, 0x1F).trim();
        } else {
            user = new String(decUserData, 0x5E, 0xE).trim().toLowerCase();
            password = new String(decUserData, 0x6C, 0x14).trim();
        }

        AuthController.getInstance().authenticate(client, user, password);
    }
}
