package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PlayerInGame extends SendablePacket {
    private String[] accounts;

    public PlayerInGame(String... accounts) {
        this.accounts = accounts;
    }

    @Override
    protected void writeImpl(AuthServerClient client) {
        writeByte((byte)0x03);
        writeShort((short) accounts.length);
        for (String account : accounts) {
            writeString(account);
        }
    }
}
