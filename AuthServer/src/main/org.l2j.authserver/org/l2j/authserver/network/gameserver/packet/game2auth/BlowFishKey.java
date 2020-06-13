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
