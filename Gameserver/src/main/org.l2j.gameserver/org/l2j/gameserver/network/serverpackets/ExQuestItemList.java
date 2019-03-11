package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author JIV
 */
public class ExQuestItemList extends AbstractItemPacket {
    private final int _sendType;
    private final L2PcInstance _activeChar;
    private final Collection<L2ItemInstance> _items;

    public ExQuestItemList(int sendType, L2PcInstance activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _items = activeChar.getInventory().getItems(L2ItemInstance::isQuestItem);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_QUEST_ITEM_LIST.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_items.size());
        } else {
            packet.putShort((short) 0);
        }
        packet.putInt(_items.size());
        for (L2ItemInstance item : _items) {
            writeItem(packet, item);
        }
        writeInventoryBlock(packet, _activeChar.getInventory());
    }
}
