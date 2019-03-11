package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.SecondaryAuthData;

import java.nio.ByteBuffer;

/**
 * Format: (ch)S S: numerical password
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordVerify extends IClientIncomingPacket {
    private String _password;

    @Override
    public void readImpl(ByteBuffer packet) {
        _password = readString(packet);
    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthData.getInstance().isEnabled()) {
            return;
        }

        client.getSecondaryAuth().checkPassword(_password, false);
    }
}
