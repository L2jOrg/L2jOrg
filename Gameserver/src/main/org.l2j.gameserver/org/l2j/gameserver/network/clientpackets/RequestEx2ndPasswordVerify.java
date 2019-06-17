package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.SecondaryAuthData;

/**
 * Format: (ch)S S: numerical password
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordVerify extends ClientPacket {
    private String _password;

    @Override
    public void readImpl() {
        _password = readString();
    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthData.getInstance().isEnabled()) {
            return;
        }

        client.getSecondaryAuth().checkPassword(_password, false);
    }
}
