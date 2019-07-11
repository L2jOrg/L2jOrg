package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author Mobius
 */
public class PackageSendableList extends AbstractItemPacket {
    private final Collection<Item> _items;
    private final int _objectId;
    private final long _adena;
    private final int _sendType;

    public PackageSendableList(int sendType, Player player, int objectId) {
        _sendType = sendType;
        _items = player.getInventory().getAvailableItems(true, true, true);
        _objectId = objectId;
        _adena = player.getAdena();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PACKAGE_SENDABLE_LIST);

        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeInt(_items.size());
            writeInt(_items.size());
            for (Item item : _items) {
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
