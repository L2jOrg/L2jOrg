package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.SessionKey;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerAuthRequest;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.s2c.LoginResultPacket;
import org.l2j.gameserver.network.l2.s2c.ServerCloseSocketPacket;
import org.l2j.gameserver.utils.Language;

import java.nio.ByteBuffer;

/**
 * cSddddd
 * cSdddddQ
 * loginName + keys must match what the loginserver used.
 */
public class AuthLogin extends L2GameClientPacket
{
    private String _loginName;
    private int _playKey1;
    private int _playKey2;
    private int _loginKey1;
    private int _loginKey2;
    private int _lang;

    @Override
    protected void readImpl(ByteBuffer buffer) {
        _loginName = readString(buffer, 32).toLowerCase();
        _playKey2 = buffer.getInt();
        _playKey1 = buffer.getInt();
        _loginKey1 = buffer.getInt();
        _loginKey2 = buffer.getInt();
        _lang = buffer.getInt();
        buffer.getInt();
    }

    @Override
    protected void runImpl()
    {


        SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);
        client.setSessionId(key);
        client.setLoginName(_loginName);
        client.setLanguage(Language.getLanguage(_lang));

        if(Shutdown.getInstance().getMode() != Shutdown.NONE && Shutdown.getInstance().getSeconds() <= 15)
            client.closeNow();
        else
        {
            if(AuthServerCommunication.getInstance().isShutdown())
            {
                client.close(LoginResultPacket.SYSTEM_ERROR_LOGIN_LATER);
                return;
            }

            GameClient oldClient = AuthServerCommunication.getInstance().addWaitingClient(client);
            if(oldClient != null)
                oldClient.close(ServerCloseSocketPacket.STATIC);

            AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest(client));
        }
    }
}