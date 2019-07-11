package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2PremiumItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gnacik
 */
public class ExGetPremiumItemList extends ServerPacket {
    private final Player _activeChar;

    private final Map<Integer, L2PremiumItem> _map;

    public ExGetPremiumItemList(Player activeChar) {
        _activeChar = activeChar;
        _map = _activeChar.getPremiumItemList();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_GET_PREMIUM_ITEM_LIST);

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
