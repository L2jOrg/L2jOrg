package org.l2j.gameserver.mobius.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AbstractItemPacket;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author NosBit
 */
public class ExResponseCommissionItemList extends AbstractItemPacket {
    private final int _sendType;
    private final Collection<L2ItemInstance> _items;

    public ExResponseCommissionItemList(int sendType, Collection<L2ItemInstance> items) {
        _sendType = sendType;
        _items = items;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RESPONSE_COMMISSION_ITEM_LIST.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_items.size());
            packet.putInt(_items.size());
            for (L2ItemInstance itemInstance : _items) {
                writeItem(packet, itemInstance);
            }
        } else {
            packet.putInt(0);
            packet.putInt(0);
        }
    }
}
