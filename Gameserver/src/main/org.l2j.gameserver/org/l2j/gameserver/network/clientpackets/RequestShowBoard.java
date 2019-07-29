package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.CommunityBoardHandler;

/**
 * RequestShowBoard client packet implementation.
 *
 * @author Zoey76
 */
public final class RequestShowBoard extends ClientPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _unknown = readInt();
    }

    @Override
    public void runImpl() {
        CommunityBoardHandler.getInstance().handleParseCommand(Config.BBS_DEFAULT, client.getPlayer());
    }
}
