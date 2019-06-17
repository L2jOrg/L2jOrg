package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_QUEST_ITEM_LIST);
        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeInt(_items.size());
        } else {
            writeShort((short) 0);
        }
        writeInt(_items.size());
        for (L2ItemInstance item : _items) {
            writeItem(item);
        }
        writeInventoryBlock(_activeChar.getInventory());
    }
}
