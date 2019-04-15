package org.l2j.gameserver.network.serverpackets.vip;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.data.xml.impl.VipData;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;

public class ReceiveVipProductList extends IClientOutgoingPacket {

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECEIVE_VIP_PRODUCT_LIST.writeId(packet);

        var player = client.getActiveChar();
        var products = PrimeShopData.getInstance().getPrimeItems();
        var vipTier = VipData.getInstance().getVipTier(player);

        packet.putLong(player.getAdena());
        packet.putLong(client.getRustyCoin()); // Rusty Coin Amount
        packet.putLong(client.getSilverCoin()); // Silver Coin Amount
        packet.put((byte) 1); // Show Reward tab

        packet.putInt(vipTier > 0 ? products.size() + 1 : products.size());

        for (var product : products.values()) {
            putProduct(packet, product);
        }

        if(vipTier > 0 ) {
            var gift = PrimeShopData.getInstance().getVipGift(vipTier);
            if(nonNull(gift)) {
                putProduct(packet, gift);
            }
        }
    }

    private void putProduct(ByteBuffer packet, PrimeShopProduct product) {
        packet.putInt(product.getId());
        packet.put(product.getCategory());
        packet.put(product.getPaymentType());
        packet.putInt(product.getPrice()); // L2 Coin | Rusty Coin seems to use the same field based on payment type
        packet.putInt(product.getSilverCoin());
        packet.put(product.getPanelType()); // NEW - 6; HOT - 5 ... Unk
        packet.put(product.getVipTier());
        packet.put((byte) 7); // Unk

        packet.put((byte) product.getItems().size());

        for (PrimeShopItem item : product.getItems()) {
            packet.putInt(item.getId());
            packet.putInt((int) item.getCount());
        }
    }
}
