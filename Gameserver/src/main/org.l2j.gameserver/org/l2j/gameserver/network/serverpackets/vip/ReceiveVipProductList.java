package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.nonNull;

public class ReceiveVipProductList extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) {
        var player = client.getPlayer();
        var products = PrimeShopData.getInstance().getPrimeItems();
        var gift = PrimeShopData.getInstance().getVipGiftOfTier(player.getVipTier());

        writeId(ServerPacketId.RECEIVE_VIP_PRODUCT_LIST);
        writeLong(player.getAdena());
        writeLong(player.getRustyCoin()); // Rusty Coin Amount
        writeLong(player.getSilverCoin()); // Silver Coin Amount
        writeByte((byte) 1); // Show Reward tab

        if(nonNull(gift)) {
            writeInt(products.size() + 1);
            putProduct(gift);
        } else {
            writeInt(products.size());
        }

        for (var product : products.values()) {
            putProduct(product);
        }
    }

    private void putProduct(PrimeShopProduct product) {
        writeInt(product.getId());
        writeByte(product.getCategory());
        writeByte(product.getPaymentType());
        writeInt(product.getPrice()); // L2 Coin | Rusty Coin seems to use the same field based on payment type
        writeInt(product.getSilverCoin());
        writeByte(product.getPanelType()); // NEW - 6; HOT - 5 ... Unk
        writeByte(product.getVipTier());
        writeByte((byte) 7); // Unk

        writeByte((byte) product.getItems().size());

        for (PrimeShopItem item : product.getItems()) {
            writeInt(item.getId());
            writeInt((int) item.getCount());
        }
    }


}
