package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.network.client.packet.L2LoginClientPacket;
import org.l2j.authserver.network.client.packet.auth2client.GGAuth;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.l2j.authserver.network.client.AuthClientState.AUTHED_GG;

/**
 * @author -Wooden- Format: ddddd
 */
public class AuthGameGuard extends L2LoginClientPacket {

    private int _sessionId;

    /**
     * @see L2LoginClientPacket#readImpl(ByteBuffer)
     * @param buffer
     */
    @Override
    protected boolean readImpl(ByteBuffer buffer) {
        if (buffer.remaining() >= 20) {
            _sessionId = buffer.getInt();
            int _data1 = buffer.getInt();
            int _data2 = buffer.getInt();
            int _data3 = buffer.getInt();
            int _data4 = buffer.getInt();
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