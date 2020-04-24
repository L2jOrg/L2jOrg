package org.l2j.gameserver.network.serverpackets.crystalization;

import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author UnAfraid
 */
public class ExGetCrystalizingEstimation extends ServerPacket {
    private final List<ItemChanceHolder> _items;

    public ExGetCrystalizingEstimation(List<ItemChanceHolder> items) {
        _items = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_CRYSTALITEM_INFO);

        writeInt(_items.size());
        for (ItemChanceHolder holder : _items) {
            writeInt(holder.getId());
            writeLong(holder.getCount());
            writeDouble(holder.getChance());
        }
    }

}