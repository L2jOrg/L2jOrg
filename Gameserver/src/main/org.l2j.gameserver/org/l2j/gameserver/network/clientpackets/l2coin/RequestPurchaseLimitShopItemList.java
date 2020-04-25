package org.l2j.gameserver.network.clientpackets.l2coin;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.l2coin.ExPurchaseLimitShopItemListNew;

/**
 * @author JoeAlisson
 */
public class RequestPurchaseLimitShopItemList extends ClientPacket {

    private byte index;

    @Override
    protected void readImpl() throws Exception {
        index = readByte();
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new ExPurchaseLimitShopItemListNew(index));
    }
}
