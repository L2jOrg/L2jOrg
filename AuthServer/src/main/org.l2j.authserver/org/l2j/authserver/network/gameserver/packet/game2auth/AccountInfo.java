package org.l2j.authserver.network.gameserver.packet.game2auth;

import org.l2j.authserver.controller.AuthController;

import java.nio.ByteBuffer;

public class AccountInfo extends GameserverReadablePacket {

    private  int players;
    private  String account;

    @Override
    protected void readImpl(ByteBuffer buffer)   {
        account = readString(buffer);
        players = buffer.get();
    }

    @Override
    protected void runImpl()  {
        AuthController.getInstance().addAccountCharactersInfo(client.getGameServerInfo().getId(), account, players);
    }
}
