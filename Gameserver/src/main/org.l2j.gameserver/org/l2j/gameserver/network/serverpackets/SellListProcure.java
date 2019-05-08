package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.CropProcure;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class SellListProcure extends IClientOutgoingPacket {
    private final long _money;
    private final Map<L2ItemInstance, Long> _sellList = new HashMap<>();

    public SellListProcure(L2PcInstance player, int castleId) {
        _money = player.getAdena();
        for (CropProcure c : CastleManorManager.getInstance().getCropProcure(castleId, false)) {
            final L2ItemInstance item = player.getInventory().getItemByItemId(c.getId());
            if ((item != null) && (c.getAmount() > 0)) {
                _sellList.put(item, c.getAmount());
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SELL_LIST_PROCURE.writeId(packet);

        packet.putLong(_money); // money
        packet.putInt(0x00); // lease ?
        packet.putShort((short) _sellList.size()); // list size

        for (L2ItemInstance item : _sellList.keySet()) {
            packet.putShort((short) item.getItem().getType1());
            packet.putInt(item.getObjectId());
            packet.putInt(item.getDisplayId());
            packet.putLong(_sellList.get(item)); // count
            packet.putShort((short) item.getItem().getType2());
            packet.putShort((short) 0); // unknown
            packet.putLong(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 19 + _sellList.size() * 30;
    }
}
