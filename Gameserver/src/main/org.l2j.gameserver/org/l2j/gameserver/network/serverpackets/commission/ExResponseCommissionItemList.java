package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author NosBit
 */
public class ExResponseCommissionItemList extends AbstractItemPacket {
    private final int sendType;
    private final Collection<L2ItemInstance> items;

    public ExResponseCommissionItemList(int sendType, Collection<L2ItemInstance> items) {
        this.sendType = sendType;
        this.items = items;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_RESPONSE_COMMISSION_ITEM_LIST);
        writeByte((byte) sendType);
        if (sendType == 2) {
            writeInt(items.size());
            writeInt(items.size());
            for (L2ItemInstance itemInstance : items) {
                writeItem(itemInstance);
            }
        } else {
            writeInt(0);
            writeInt(0);
        }
    }

}
