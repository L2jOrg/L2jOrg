package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExBRNewIconCashBtnWnd extends IClientOutgoingPacket {

    public static ExBRNewIconCashBtnWnd STATIC = new ExBRNewIconCashBtnWnd();

    private ExBRNewIconCashBtnWnd() {

    }

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_NEW_ICON_CASH_BTN_WND.writeId(packet);
        packet.putShort((short) 0x00); // has update ?
    }

    @Override
    protected int size(L2GameClient client) {
        return 7;
    }
}
