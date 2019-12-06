package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class GMViewWarehouseWithdrawList extends AbstractItemPacket {
    private final Collection<Item> _items;
    private final String playerName;
    private final long _money;
    private final int sendType;

    public GMViewWarehouseWithdrawList(int sendType, Player cha) {
        this.sendType = sendType;
        _items = cha.getWarehouse().getItems();
        playerName = cha.getName();
        _money = cha.getWarehouse().getAdena();
    }

    public GMViewWarehouseWithdrawList(int sendType, Clan clan) {
        this.sendType = sendType;
        playerName = clan.getLeaderName();
        _items = clan.getWarehouse().getItems();
        _money = clan.getWarehouse().getAdena();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.GM_VIEW_WAREHOUSE_WITHDRAW_LIST);
        writeByte(sendType);

        if(sendType == 2) {
            writeInt(_items.size());
            writeInt(_items.size());
            for (Item item : _items) {
                writeItem(item);
                writeInt(item.getObjectId());
            }
        } else {
            writeString(playerName);
            writeLong(_money);
            writeInt((short) _items.size());
        }
    }

}
