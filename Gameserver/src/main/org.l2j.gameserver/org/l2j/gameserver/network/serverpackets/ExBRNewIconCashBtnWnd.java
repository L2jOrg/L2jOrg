package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExBRNewIconCashBtnWnd extends IClientOutgoingPacket {

    public static ExBRNewIconCashBtnWnd NOT_SHOW = new ExBRNewIconCashBtnWnd((short) 0);
    public static ExBRNewIconCashBtnWnd SHOW = new ExBRNewIconCashBtnWnd((short) 1);

    private final short show;

    private ExBRNewIconCashBtnWnd(short show) {
        this.show = show;
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BR_NEW_ICON_CASH_BTN_WND);
        writeShort(show); // Show icon
    }

}
