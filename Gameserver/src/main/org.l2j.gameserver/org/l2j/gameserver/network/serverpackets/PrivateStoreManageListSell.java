package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class PrivateStoreManageListSell extends AbstractItemPacket {
    private final int _sendType;
    private final int _objId;
    private final long _playerAdena;
    private final boolean _packageSale;
    private final Collection<TradeItem> _itemList;
    private final TradeItem[] _sellList;

    public PrivateStoreManageListSell(int sendType, L2PcInstance player, boolean isPackageSale) {
        _sendType = sendType;
        _objId = player.getObjectId();
        _playerAdena = player.getAdena();
        player.getSellList().updateItems();
        _packageSale = isPackageSale;
        _itemList = player.getInventory().getAvailableItems(player.getSellList());
        _sellList = player.getSellList().getItems();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PRIVATE_STORE_MANAGE_LIST);
        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeInt(_itemList.size());
            writeInt(_itemList.size());
            for (TradeItem item : _itemList) {
                writeItem(item);
                writeLong(item.getItem().getReferencePrice() * 2);
            }
        } else {
            writeInt(_objId);
            writeInt(_packageSale ? 1 : 0);
            writeLong(_playerAdena);
            writeInt(0x00);
            for (TradeItem item : _itemList) {
                writeItem(item);
                writeLong(item.getItem().getReferencePrice() * 2);
            }
            writeInt(0x00);
            for (TradeItem item2 : _sellList) {
                writeItem(item2);
                writeLong(item2.getPrice());
                writeLong(item2.getItem().getReferencePrice() * 2);
            }
        }
    }

}
