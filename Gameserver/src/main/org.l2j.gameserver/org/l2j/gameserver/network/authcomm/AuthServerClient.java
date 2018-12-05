package org.l2j.gameserver.network.authcomm;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.network.authcomm.gs2as.AuthRequest;
import org.l2j.mmocore.Client;
import org.l2j.mmocore.Connection;

class AuthServerClient extends Client<Connection<AuthServerClient>> {

    AuthServerClient(Connection<AuthServerClient> connection) {
        super(connection);
    }

    public void sendPacket(SendablePacket packet) {
        writePacket(packet);
    }

    @Override
    public int encrypt(byte[] data, int offset, int size) {
        return 0;
    }

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        return false;
    }

    @Override
    protected void onDisconnection() {
        AuthServerCommunication.getInstance().restart();
    }

    @Override
    public void onConnected() {
        writePacket(new AuthRequest());
    }
}
