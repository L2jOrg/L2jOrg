package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.SecondaryAuthManager;

/**
 * Format: (ch)S S: numerical password
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordVerify extends ClientPacket {
    private String password;

    @Override
    public void readImpl() {
        password = readString();
    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled()) {
            return;
        }

        client.checkPassword(password, false);
    }
}
