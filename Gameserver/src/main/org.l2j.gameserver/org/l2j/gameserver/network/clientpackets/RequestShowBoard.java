package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.CommunityBoardHandler;

import java.nio.ByteBuffer;

/**
 * RequestShowBoard client packet implementation.
 *
 * @author Zoey76
 */
public final class RequestShowBoard extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _unknown = readInt();
    }

    @Override
    public void runImpl() {
        CommunityBoardHandler.getInstance().handleParseCommand(Config.BBS_DEFAULT, client.getActiveChar());
    }
}
