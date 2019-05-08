package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public class GMViewWarehouseWithdrawList extends AbstractItemPacket {
    private final Collection<L2ItemInstance> _items;
    private final String playerName;
    private final long _money;

    public GMViewWarehouseWithdrawList(L2PcInstance cha) {
        _items = cha.getWarehouse().getItems();
        playerName = cha.getName();
        _money = cha.getWarehouse().getAdena();
    }

    public GMViewWarehouseWithdrawList(L2Clan clan) {
        playerName = clan.getLeaderName();
        _items = clan.getWarehouse().getItems();
        _money = clan.getWarehouse().getAdena();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_WAREHOUSE_WITHDRAW_LIST.writeId(packet);
        writeString(playerName, packet);
        packet.putLong(_money);
        packet.putShort((short) _items.size());
        for (L2ItemInstance item : _items) {
            writeItem(packet, item);
            packet.putInt(item.getObjectId());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 21 + playerName.length() * 2 + _items.size() * 104;
    }
}
