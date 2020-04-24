package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExBRNewIconCashBtnWnd extends ServerPacket {

    public static ExBRNewIconCashBtnWnd NOT_SHOW = new ExBRNewIconCashBtnWnd((short) 0);
    public static ExBRNewIconCashBtnWnd SHOW = new ExBRNewIconCashBtnWnd((short) 1);

    private final short show;

    private ExBRNewIconCashBtnWnd(short show) {
        this.show = show;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_EXIST_NEW_PRODUCT_ACK);
        writeShort(show); // Show icon
    }

}
