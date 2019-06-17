package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author Mobius
 */
public class PackageSendableList extends AbstractItemPacket {
    private final Collection<L2ItemInstance> _items;
    private final int _objectId;
    private final long _adena;
    private final int _sendType;

    public PackageSendableList(int sendType, L2PcInstance player, int objectId) {
        _sendType = sendType;
        _items = player.getInventory().getAvailableItems(true, true, true);
        _objectId = objectId;
        _adena = player.getAdena();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.PACKAGE_SENDABLE_LIST);

        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeInt(_items.size());
            writeInt(_items.size());
            for (L2ItemInstance item : _items) {
                writeItem(item);
                writeInt(item.getObjectId());
            }
        } else {
            writeInt(_objectId);
            writeLong(_adena);
            writeInt(_items.size());
        }
    }

}
