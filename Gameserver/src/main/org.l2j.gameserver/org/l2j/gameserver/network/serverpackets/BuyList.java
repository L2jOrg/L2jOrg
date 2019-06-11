package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public final class BuyList extends AbstractItemPacket {
    private final int _listId;
    private final Collection<Product> _list;
    private final long _money;
    private final int _inventorySlots;
    private final double _castleTaxRate;

    public BuyList(ProductList list, L2PcInstance player, double castleTaxRate) {
        _listId = list.getListId();
        _list = list.getProducts();
        _money = player.getAdena();
        _inventorySlots = player.getInventory().getItems((item) -> !item.isQuestItem()).size();
        _castleTaxRate = castleTaxRate;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_BUY_SELL_LIST);

        writeInt(0x00); // Type BUY
        writeLong(_money); // current money
        writeInt(_listId);
        writeInt(_inventorySlots);
        writeShort((short) _list.size());
        for (Product product : _list) {
            if ((product.getCount() > 0) || !product.hasLimitedStock()) {
                writeItem(product);
                writeLong((long) (product.getPrice() * (1.0 + _castleTaxRate + product.getBaseTaxRate())));
            }
        }
    }

}
