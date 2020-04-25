package org.l2j.gameserver.network.serverpackets.l2coin;

import org.l2j.gameserver.data.xml.impl.LCoinShopData;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author JoeAlisson
 */
public class ExPurchaseLimitShopItemListNew extends ServerPacket {

    private final byte index;

    public ExPurchaseLimitShopItemListNew(byte index) {
        this.index = index;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST_NEW);
        writeByte(index);

        final var products = LCoinShopData.getInstance().getProductInfos();
        writeInt(products.size());

        products.values().forEach(product -> {
            writeInt(product.getId());
            writeInt(product.getProduction().getId());
            writeIngredients(product.getIngredients());
            writeInt(product.getRemainAmount());
            writeInt(product.getRemainTime());
            writeInt(product.getRemainServerItemAmount());
        });
    }

    private void writeIngredients(List<ItemHolder> ingredients) {
        for (int i = 0; i < 3; i++) {
            if(i < ingredients.size()) {
                writeInt(ingredients.get(i).getId());
            } else {
                writeInt(0);
            }
        }

        for (int i = 0; i < 3; i++) {
            if(i < ingredients.size()) {
                writeLong(ingredients.get(i).getCount());
            } else {
                writeLong(0);
            }

        }
    }
}
