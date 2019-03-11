package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.CommunityBoardHandler;

import java.nio.ByteBuffer;

/**
 * RequestBBSwrite client packet implementation.
 *
 * @author -Wooden-, Zoey76
 */
public final class RequestBBSwrite extends IClientIncomingPacket {
    private String _url;
    private String _arg1;
    private String _arg2;
    private String _arg3;
    private String _arg4;
    private String _arg5;

    @Override
    public final void readImpl(ByteBuffer packet) {
        _url = readString(packet);
        _arg1 = readString(packet);
        _arg2 = readString(packet);
        _arg3 = readString(packet);
        _arg4 = readString(packet);
        _arg5 = readString(packet);
    }

    @Override
    public final void runImpl() {
        CommunityBoardHandler.getInstance().handleWriteCommand(client.getActiveChar(), _url, _arg1, _arg2, _arg3, _arg4, _arg5);
    }
}