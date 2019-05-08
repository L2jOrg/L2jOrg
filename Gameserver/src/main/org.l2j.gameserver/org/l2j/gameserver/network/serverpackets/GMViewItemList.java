package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.GM_VIEW_ITEM_LIST.writeId(packet);
        packet.put((byte) sendType);
        if (sendType == 2) {
            packet.putInt(items.size());
        } else {
            writeString(playerName, packet);
            packet.putInt(_limit); // inventory limit
        }
        packet.putInt(items.size());
        for (L2ItemInstance item : items) {
            writeItem(packet, item);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return  25 + items.size() * 100 + (sendType == 2 ? 4 :  playerName.length() * 2);
    }
}
