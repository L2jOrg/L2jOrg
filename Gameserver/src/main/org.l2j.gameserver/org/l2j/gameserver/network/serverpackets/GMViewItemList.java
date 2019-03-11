package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GMViewItemList extends AbstractItemPacket {
    private final int _sendType;
    private final List<L2ItemInstance> _items = new ArrayList<>();
    private final int _limit;
    private final String _playerName;

    public GMViewItemList(int sendType, L2PcInstance cha) {
        _sendType = sendType;
        _playerName = cha.getName();
        _limit = cha.getInventoryLimit();
        for (L2ItemInstance item : cha.getInventory().getItems()) {
            _items.add(item);
        }
    }

    public GMViewItemList(int sendType, L2PetInstance cha) {
        _sendType = sendType;
        _playerName = cha.getName();
        _limit = cha.getInventoryLimit();
        for (L2ItemInstance item : cha.getInventory().getItems()) {
            _items.add(item);
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_ITEM_LIST.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_items.size());
        } else {
            writeString(_playerName, packet);
            packet.putInt(_limit); // inventory limit
        }
        packet.putInt(_items.size());
        for (L2ItemInstance item : _items) {
            writeItem(packet, item);
        }
    }
}
