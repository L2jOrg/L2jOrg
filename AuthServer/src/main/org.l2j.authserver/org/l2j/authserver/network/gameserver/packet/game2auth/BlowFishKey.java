package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.network.gameserver.packet.auth2game.RequestServerIdentity;

import javax.crypto.Cipher;

public class BlowFishKey extends GameserverReadablePacket {

    private byte[] receivedKey;

    @Override
    protected void readImpl() {
        var size = readInt();
        receivedKey = new byte[size];
        readBytes(receivedKey);
    }

    @Override
    protected void runImpl() throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
        rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
        byte[] tempDecryptKey = rsaCipher.doFinal(receivedKey);
        // there are nulls before the receivedKey we must remove them
        int i = 0;
        int len = tempDecryptKey.length;
        for (; i < len; i++) {
            if (tempDecryptKey[i] != 0) {
                break;
            }
        }
        var key = new byte[len - i];

        System.arraycopy(tempDecryptKey, i, key, 0, len - i);
        client.setCryptKey(key);
        client.sendPacket(new RequestServerIdentity());
    }
}
