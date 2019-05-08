package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.2.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreListBuy extends AbstractItemPacket {
    private final int _objId;
    private final long _playerAdena;
    private final Collection<TradeItem> _items;

    public PrivateStoreListBuy(L2PcInstance player, L2PcInstance storePlayer) {
        _objId = storePlayer.getObjectId();
        _playerAdena = player.getAdena();
        storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
        _items = storePlayer.getBuyList().getAvailableItems(player.getInventory());
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PRIVATE_STORE_BUY_LIST.writeId(packet);

        packet.putInt(_objId);
        packet.putLong(_playerAdena);
        packet.putInt(0x00); // Viewer's item count?
        packet.putInt(_items.size());

        int slotNumber = 0;
        for (TradeItem item : _items) {
            slotNumber++;
            writeItem(packet, item);
            packet.putInt(slotNumber); // Slot in shop
            packet.putLong(item.getPrice());
            packet.putLong(item.getItem().getReferencePrice() * 2);
            packet.putLong(item.getStoreCount());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 30 + _items.size() * 130;
    }
}
