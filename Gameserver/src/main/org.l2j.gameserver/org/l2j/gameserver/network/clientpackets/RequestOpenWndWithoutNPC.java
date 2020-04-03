package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExPurchaseLimitShopItemList;

public class RequestOpenWndWithoutNPC extends ClientPacket {
    private int dialogId;
    @Override
    protected void readImpl() throws Exception {
        dialogId = readByte();
    }

    @Override
    protected void runImpl() throws Exception {
        if (dialogId == 4) {
            getClient().sendPacket(new ExPurchaseLimitShopItemList());
        }
    }
}
