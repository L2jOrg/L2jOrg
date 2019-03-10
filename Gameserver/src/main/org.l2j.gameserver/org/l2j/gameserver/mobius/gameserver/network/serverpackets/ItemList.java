package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemList extends AbstractItemPacket {
    private final int _sendType;
    private final L2PcInstance _activeChar;
    private final List<L2ItemInstance> _items;

    public ItemList(int sendType, L2PcInstance activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _items = activeChar.getInventory().getItems(item -> !item.isQuestItem()).stream().collect(Collectors.toList());
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ITEM_LIST.writeId(packet);
        if (_sendType == 2) {
            packet.put((byte) _sendType);
            packet.putInt(_items.size());
            packet.putInt(_items.size());
            for (L2ItemInstance item : _items) {
                writeItem(packet, item);
            }
        } else {
            packet.put((byte) 0x01); // _showWindow ? 0x01 : 0x00
            packet.putInt(0x00);
            packet.putInt(_items.size());
        }
        writeInventoryBlock(packet, _activeChar.getInventory());
    }
}
