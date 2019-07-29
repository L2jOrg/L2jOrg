package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.CommunityBoardHandler;

/**
 * RequestBBSwrite client packet implementation.
 *
 * @author -Wooden-, Zoey76
 */
public final class RequestBBSwrite extends ClientPacket {
    private String _url;
    private String _arg1;
    private String _arg2;
    private String _arg3;
    private String _arg4;
    private String _arg5;

    @Override
    public final void readImpl() {
        _url = readString();
        _arg1 = readString();
        _arg2 = readString();
        _arg3 = readString();
        _arg4 = readString();
        _arg5 = readString();
    }

    @Override
    public final void runImpl() {
        CommunityBoardHandler.getInstance().handleWriteCommand(client.getPlayer(), _url, _arg1, _arg2, _arg3, _arg4, _arg5);
    }
}