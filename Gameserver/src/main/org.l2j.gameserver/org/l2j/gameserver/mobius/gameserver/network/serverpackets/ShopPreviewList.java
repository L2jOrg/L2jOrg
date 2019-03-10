package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.buylist.Product;
import org.l2j.gameserver.mobius.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public class ShopPreviewList extends IClientOutgoingPacket {
    private final int _listId;
    private final Collection<Product> _list;
    private final long _money;
    private int _expertise;

    public ShopPreviewList(ProductList list, long currentMoney, int expertiseIndex) {
        _listId = list.getListId();
        _list = list.getProducts();
        _money = currentMoney;
        _expertise = expertiseIndex;
    }

    public ShopPreviewList(Collection<Product> lst, int listId, long currentMoney) {
        _listId = listId;
        _list = lst;
        _money = currentMoney;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOP_PREVIEW_LIST.writeId(packet);

        packet.putInt(5056);
        packet.putLong(_money); // current money
        packet.putInt(_listId);

        int newlength = 0;
        for (Product product : _list) {
            if ((product.getItem().getCrystalType().getId() <= _expertise) && product.getItem().isEquipable()) {
                newlength++;
            }
        }
        packet.putShort((short) newlength);

        for (Product product : _list) {
            if ((product.getItem().getCrystalType().getId() <= _expertise) && product.getItem().isEquipable()) {
                packet.putInt(product.getItemId());
                packet.putShort((short) product.getItem().getType2()); // item type2

                if (product.getItem().getType1() != L2Item.TYPE1_ITEM_QUESTITEM_ADENA) {
                    packet.putLong(product.getItem().getBodyPart()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
                } else {
                    packet.putLong(0x00); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
                }

                packet.putLong(Config.WEAR_PRICE);
            }
        }
    }
}
