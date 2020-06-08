package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.Client;
import io.github.joealisson.mmocore.Connection;
import org.l2j.gameserver.network.authcomm.gs2as.AuthRequest;

/**
 * @author JoeAlisson
 */
public class AuthServerClient extends Client<Connection<AuthServerClient>> {

    AuthServerClient(Connection<AuthServerClient> connection) {
        super(connection);
    }

    public void sendPacket(SendablePacket packet) {
        writePacket(packet);
    }

    @Override
    public int encryptedSize(int dataSize) {
        return dataSize;
    }

    @Override
    public byte[] encrypt(byte[] data, int offset, int size) {
        return data;
    }

    @Override
    public boolean decrypt(byte[] data, int offset, int size) {
        return true;
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
