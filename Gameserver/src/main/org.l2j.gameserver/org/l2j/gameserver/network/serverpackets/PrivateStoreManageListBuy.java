package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public class PrivateStoreManageListBuy extends AbstractItemPacket {
    private final int _sendType;
    private final int _objId;
    private final long _playerAdena;
    private final Collection<L2ItemInstance> _itemList;
    private final TradeItem[] _buyList;

    public PrivateStoreManageListBuy(int sendType, L2PcInstance player) {
        _sendType = sendType;
        _objId = player.getObjectId();
        _playerAdena = player.getAdena();
        _itemList = player.getInventory().getUniqueItems(false, true);
        _buyList = player.getBuyList().getItems();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PRIVATE_STORE_BUY_MANAGE_LIST.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_itemList.size());
            packet.putInt(_itemList.size());
            for (L2ItemInstance item : _itemList) {
                writeItem(packet, item);
                packet.putLong(item.getItem().getReferencePrice() * 2);
            }
        } else {
            packet.putInt(_objId);
            packet.putLong(_playerAdena);
            packet.putInt(0x00);
            for (L2ItemInstance item : _itemList) {
                writeItem(packet, item);
                packet.putLong(item.getItem().getReferencePrice() * 2);
            }
            packet.putInt(0x00);
            for (TradeItem item2 : _buyList) {
                writeItem(packet, item2);
                packet.putLong(item2.getPrice());
                packet.putLong(item2.getItem().getReferencePrice() * 2);
                packet.putLong(item2.getCount());
            }
        }
    }
}
