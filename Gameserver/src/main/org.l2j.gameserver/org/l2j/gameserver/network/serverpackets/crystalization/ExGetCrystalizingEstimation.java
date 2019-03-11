package org.l2j.gameserver.network.serverpackets.crystalization;

import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExGetCrystalizingEstimation extends IClientOutgoingPacket {
    private final List<ItemChanceHolder> _items;

    public ExGetCrystalizingEstimation(List<ItemChanceHolder> items) {
        _items = items;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_GET_CRYSTALIZING_ESTIMATION.writeId(packet);

        packet.putInt(_items.size());
        for (ItemChanceHolder holder : _items) {
            packet.putInt(holder.getId());
            packet.putLong(holder.getCount());
            packet.putDouble(holder.getChance());
        }
    }
}
