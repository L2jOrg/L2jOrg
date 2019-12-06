package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class ShopPreviewList extends ServerPacket {
    private final int _listId;
    private final Collection<Product> _list;
    private final long _money;

    public ShopPreviewList(ProductList list, long currentMoney) {
        _listId = list.getListId();
        _list = list.getProducts();
        _money = currentMoney;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOP_PREVIEW_LIST);

        writeInt(5056);
        writeLong(_money); // current money
        writeInt(_listId);

        int newlength = 0;
        for (Product product : _list) {
            if (product.isEquipable()) {
                newlength++;
            }
        }
        writeShort((short) newlength);

        for (Product product : _list) {
            if (product.isEquipable()) {
                writeInt(product.getItemId());
                writeShort(product.getType2()); // item type2

                if (product.getType1() != ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA) {
                    writeLong(product.getBodyPart().getId()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
                } else {
                    writeLong(0x00); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
                }

                writeLong(Config.WEAR_PRICE);
            }
        }
    }

}
