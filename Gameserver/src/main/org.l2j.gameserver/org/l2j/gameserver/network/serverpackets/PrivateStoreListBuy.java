package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

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

    public PrivateStoreListBuy(Player player, Player storePlayer) {
        _objId = storePlayer.getObjectId();
        _playerAdena = player.getAdena();
        storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
        _items = storePlayer.getBuyList().getAvailableItems(player.getInventory());
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PRIVATE_STORE_BUY_LIST);

        writeInt(_objId);
        writeLong(_playerAdena);
        writeInt(0x00); // Viewer's item count?
        writeInt(_items.size());

        int slotNumber = 0;
        for (TradeItem item : _items) {
            slotNumber++;
            writeItem(item);
            writeInt(slotNumber); // Slot in shop
            writeLong(item.getPrice());
            writeLong(item.getItem().getReferencePrice() * 2);
            writeLong(item.getStoreCount());
        }
    }

}
