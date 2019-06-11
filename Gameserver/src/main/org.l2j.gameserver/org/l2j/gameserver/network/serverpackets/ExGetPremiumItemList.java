package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2PremiumItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_GET_PREMIUM_ITEM_LIST);

        writeInt(_map.size());
        for (Entry<Integer, L2PremiumItem> entry : _map.entrySet()) {
            final L2PremiumItem item = entry.getValue();
            writeLong(entry.getKey());
            writeInt(item.getItemId());
            writeLong(item.getCount());
            writeInt(0x00); // ?
            writeString(item.getSender());
        }
    }

}
