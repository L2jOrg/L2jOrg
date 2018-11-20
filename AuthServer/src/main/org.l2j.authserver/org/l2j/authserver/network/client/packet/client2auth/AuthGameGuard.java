package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.network.client.packet.L2LoginClientPacket;
import org.l2j.authserver.network.client.packet.auth2client.GGAuth;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason;

import java.util.Objects;

import static org.l2j.authserver.network.client.AuthClientState.AUTHED_GG;

/**
 * @author -Wooden- Format: ddddd
 */
public class AuthGameGuard extends L2LoginClientPacket {

    private int _sessionId;

    /**
     * @see L2LoginClientPacket#readImpl()
     */
    @Override
    protected boolean readImpl() {
        if (availableData() >= 20) {
            _sessionId = readInt();
            int _data1 = readInt();
            int _data2 = readInt();
            int _data3 = readInt();
            int _data4 = readInt();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (Objects.equals(_sessionId, client.getSessionId())) {
            client.setState(AUTHED_GG);
            client.sendPacket(new GGAuth(_sessionId));
        } else {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        }
    }
}