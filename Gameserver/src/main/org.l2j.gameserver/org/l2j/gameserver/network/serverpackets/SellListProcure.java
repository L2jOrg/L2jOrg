package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.CropProcure;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.HashMap;
import java.util.Map;

public class SellListProcure extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SELL_LIST_PROCURE);

        writeLong(_money); // money
        writeInt(0x00); // lease ?
        writeShort((short) _sellList.size()); // list size

        for (L2ItemInstance item : _sellList.keySet()) {
            writeShort((short) item.getItem().getType1());
            writeInt(item.getObjectId());
            writeInt(item.getDisplayId());
            writeLong(_sellList.get(item)); // count
            writeShort((short) item.getItem().getType2());
            writeShort((short) 0); // unknown
            writeLong(0); // price, u shouldnt get any adena for crops, only raw materials
        }
    }

}
