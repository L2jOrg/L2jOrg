package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.controller.AuthController;
import org.l2j.authserver.network.client.packet.L2LoginClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;

import static org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason.REASON_SYSTEM_ERROR;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket {
    private static final Logger logger = LoggerFactory.getLogger(RequestAuthLogin.class);
    private final byte[] userData = new byte[128];
    private final byte[] authData = new byte[128];
    private boolean useNewAuth;

    @Override
    public boolean readImpl() {
        if (availableData() >= 256) {
            useNewAuth = true;
            readBytes(userData);
            readBytes(authData);
            return true;
        }

        if(availableData() >= 128) {
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
            logger.warn(e.getLocalizedMessage(), e);
            client.close(REASON_SYSTEM_ERROR);
            return;
        }

        String user;
        String password;
        if(useNewAuth) {
            user = new String(decUserData, 0x4E, 32).trim().toLowerCase();
            password = new String(decAuthData, 0x5C, 16).trim();
        } else {
            user = new String(decUserData, 0x5E, 14).trim().toLowerCase();
            password = new String(decUserData, 0x6C, 16).trim();
        }

        AuthController.getInstance().authenticate(client, user, password);
    }
}
