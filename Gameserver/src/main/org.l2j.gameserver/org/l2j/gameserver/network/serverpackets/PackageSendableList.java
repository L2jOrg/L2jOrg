package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PACKAGE_SENDABLE_LIST.writeId(packet);

        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_items.size());
            packet.putInt(_items.size());
            for (L2ItemInstance item : _items) {
                writeItem(packet, item);
                packet.putInt(item.getObjectId());
            }
        } else {
            packet.putInt(_objectId);
            packet.putLong(_adena);
            packet.putInt(_items.size());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 18 + (_sendType == 2 ?  _items.size() * 104 : 8);
    }
}
