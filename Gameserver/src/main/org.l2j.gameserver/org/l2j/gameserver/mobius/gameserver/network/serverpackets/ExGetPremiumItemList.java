package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2PremiumItem;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gnacik
 */
public class ExGetPremiumItemList extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    private final Map<Integer, L2PremiumItem> _map;

    public ExGetPremiumItemList(L2PcInstance activeChar) {
        _activeChar = activeChar;
        _map = _activeChar.getPremiumItemList();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_GET_PREMIUM_ITEM_LIST.writeId(packet);

        packet.putInt(_map.size());
        for (Entry<Integer, L2PremiumItem> entry : _map.entrySet()) {
            final L2PremiumItem item = entry.getValue();
            packet.putLong(entry.getKey());
            packet.putInt(item.getItemId());
            packet.putLong(item.getCount());
            packet.putInt(0x00); // ?
            writeString(item.getSender(), packet);
        }
    }
}
