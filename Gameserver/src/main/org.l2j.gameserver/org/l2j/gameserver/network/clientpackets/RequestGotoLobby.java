package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;

import java.nio.ByteBuffer;

/**
 * (ch)
 *
 * @author KenM
 */
public class RequestGotoLobby extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        client.sendPacket(new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId()));
    }
}
