package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class GMViewItemList extends AbstractItemPacket {
    private final int sendType;
    private final Collection<L2ItemInstance> items;
    private final int _limit;
    private final String playerName;

    public GMViewItemList(int sendType, L2PcInstance cha) {
        this.sendType = sendType;
        playerName = cha.getName();
        _limit = cha.getInventoryLimit();
        items = cha.getInventory().getItems();
    }

    public GMViewItemList(int sendType, L2PetInstance cha) {
        this.sendType = sendType;
        playerName = cha.getName();
        _limit = cha.getInventoryLimit();
        items = cha.getInventory().getItems();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.GM_VIEW_ITEM_LIST);
        writeByte((byte) sendType);
        if (sendType == 2) {
            writeInt(items.size());
        } else {
            writeString(playerName);
            writeInt(_limit); // inventory limit
        }
        writeInt(items.size());
        for (L2ItemInstance item : items) {
            writeItem(item);
        }
    }

}
