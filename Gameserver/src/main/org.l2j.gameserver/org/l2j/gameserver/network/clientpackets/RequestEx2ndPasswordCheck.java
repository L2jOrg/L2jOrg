package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordCheck;

/**
 * Format: (ch)
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordCheck extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled() || client.isSecondaryAuthed()) {
            client.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_OK));
            return;
        }

        client.openSecondaryAuthDialog();
    }
}
