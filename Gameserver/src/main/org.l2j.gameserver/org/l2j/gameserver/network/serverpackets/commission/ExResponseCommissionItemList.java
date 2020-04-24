package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

import java.util.Collection;

/**
 * @author NosBit
 */
public class ExResponseCommissionItemList extends AbstractItemPacket {
    private final int sendType;
    private final Collection<Item> items;

    public ExResponseCommissionItemList(int sendType, Collection<Item> items) {
        this.sendType = sendType;
        this.items = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_ITEM_LIST);
        writeByte((byte) sendType);
        if (sendType == 2) {
            writeInt(items.size());
            writeInt(items.size());
            for (Item itemInstance : items) {
                writeItem(itemInstance);
            }
        } else {
            writeInt(0);
            writeInt(0);
        }
    }

}
