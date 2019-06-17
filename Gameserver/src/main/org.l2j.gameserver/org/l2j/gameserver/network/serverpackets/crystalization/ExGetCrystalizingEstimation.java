package org.l2j.gameserver.network.serverpackets.crystalization;

import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_GET_CRYSTALIZING_ESTIMATION);

        writeInt(_items.size());
        for (ItemChanceHolder holder : _items) {
            writeInt(holder.getId());
            writeLong(holder.getCount());
            writeDouble(holder.getChance());
        }
    }

}