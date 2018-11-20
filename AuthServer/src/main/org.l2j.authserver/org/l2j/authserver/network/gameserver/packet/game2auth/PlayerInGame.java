package org.l2j.authserver.network.gameserver.packet.game2auth;

import java.util.ArrayList;
import java.util.List;

public class PlayerInGame extends GameserverReadablePacket {
    private List<String> accounts;

    @Override
    protected void readImpl() {
        int size = readShort();
        accounts = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            accounts.add(readString());
        }
    }

    @Override
    protected void runImpl()  {
        client.getGameServerInfo().addAccounts(accounts);
    }
}