package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SecondaryAuthData;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.Ex2ndPasswordAck;
import org.l2j.gameserver.mobius.gameserver.security.SecondaryPasswordAuth;

import java.nio.ByteBuffer;

/**
 * (ch)cS{S} c: change pass? S: current password S: new password
 *
 * @author mrTJO
 */
public class RequestEx2ndPasswordReq extends IClientIncomingPacket {
    private int _changePass;
    private String _password;
    private String _newPassword;

    @Override
    public void readImpl(ByteBuffer packet) {
        _changePass = packet.get();
        _password = readString(packet);
        if (_changePass == 2) {
            _newPassword = readString(packet);
        }
    }

    @Override
    public void runImpl() {
        if (!SecondaryAuthData.getInstance().isEnabled()) {
            return;
        }

        final SecondaryPasswordAuth secondAuth = client.getSecondaryAuth();
        boolean success = false;

        if ((_changePass == 0) && !secondAuth.passwordExist()) {
            success = secondAuth.savePassword(_password);
        } else if ((_changePass == 2) && secondAuth.passwordExist()) {
            success = secondAuth.changePassword(_password, _newPassword);
        }

        if (success) {
            client.sendPacket(new Ex2ndPasswordAck(_changePass, Ex2ndPasswordAck.SUCCESS));
        }
    }
}
