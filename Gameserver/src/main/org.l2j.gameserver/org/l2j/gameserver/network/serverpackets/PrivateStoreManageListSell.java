package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PRIVATE_STORE_MANAGE_LIST.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_itemList.size());
            packet.putInt(_itemList.size());
            for (TradeItem item : _itemList) {
                writeItem(packet, item);
                packet.putLong(item.getItem().getReferencePrice() * 2);
            }
        } else {
            packet.putInt(_objId);
            packet.putInt(_packageSale ? 1 : 0);
            packet.putLong(_playerAdena);
            packet.putInt(0x00);
            for (TradeItem item : _itemList) {
                writeItem(packet, item);
                packet.putLong(item.getItem().getReferencePrice() * 2);
            }
            packet.putInt(0x00);
            for (TradeItem item2 : _sellList) {
                writeItem(packet, item2);
                packet.putLong(item2.getPrice());
                packet.putLong(item2.getItem().getReferencePrice() * 2);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 30 + _itemList.size() * 110  + (_sendType == 2 ? 0 : _sellList.length * 120);
    }
}
