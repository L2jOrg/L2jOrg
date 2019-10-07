package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAck;

/**
 * (ch)cS{S} c: change pass? S: current password S: new password
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordReq extends ClientPacket {
    private int changePass;
    private String password;
    private String newPassword;

    @Override
    public void readImpl() {
        changePass = readByte();
        password = readString();
        if (changePass == 2) {
            newPassword = readString();
        }
    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled()) {
            return;
        }

        boolean success = false;

        if ((changePass == 0) && !client.hasSecondPassword()) {
            success = client.saveSecondPassword(password);
        } else if ((changePass == 2) && client.hasSecondPassword()) {
            success = client.changeSecondPassword(password, newPassword);
        }

        if (success) {
            client.sendPacket(new Ex2ndPasswordAck(changePass, Ex2ndPasswordAck.SUCCESS));
        }
    }
}
