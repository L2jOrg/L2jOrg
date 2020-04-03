package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;
import org.l2j.gameserver.network.GameClient;

public class ExPurchaseLimitShopItemBuy extends ServerPacket {
    private final boolean isFailToBuy;
    private final LCoinShopProductInfo productInfo;

    public ExPurchaseLimitShopItemBuy(LCoinShopProductInfo info, boolean isFailToBuy) {
        this.productInfo = info;
        this.isFailToBuy = isFailToBuy;
    }

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeByte(isFailToBuy ? 1 : 0);
        writeByte(0x3);
        writeInt(productInfo.getId());
        writeInt(productInfo.getProduction().getId());
        writeInt(productInfo.getLimitPerDay());
    }
}
