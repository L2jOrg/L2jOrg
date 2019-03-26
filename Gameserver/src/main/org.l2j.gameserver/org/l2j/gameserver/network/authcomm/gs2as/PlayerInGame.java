package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

import java.nio.ByteBuffer;

public class PlayerInGame extends SendablePacket {
    private String[] accounts;

    public PlayerInGame(String... accounts) {
        this.accounts = accounts;
    }

    @Override
    protected void writeImpl(AuthServerClient client, ByteBuffer buffer) {
        buffer.put((byte)0x03);
        buffer.putShort((short) accounts.length);
        for (String account : accounts) {
            writeString(account, buffer);
        }
    }
}
