package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;

/**
 * (ch)
 *
 * @author KenM
 */
public class RequestGotoLobby extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        client.sendPacket(new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId()));
    }
}
