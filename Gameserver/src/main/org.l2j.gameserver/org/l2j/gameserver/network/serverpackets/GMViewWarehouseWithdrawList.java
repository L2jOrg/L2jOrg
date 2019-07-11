package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class GMViewWarehouseWithdrawList extends AbstractItemPacket {
    private final Collection<Item> _items;
    private final String playerName;
    private final long _money;

    public GMViewWarehouseWithdrawList(Player cha) {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.GM_VIEW_WAREHOUSE_WITHDRAW_LIST);
        writeString(playerName);
        writeLong(_money);
        writeShort((short) _items.size());
        for (Item item : _items) {
            writeItem(item);
            writeInt(item.getObjectId());
        }
    }

}
