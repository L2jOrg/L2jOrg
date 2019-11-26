package org.l2j.gameserver.network.serverpackets.trade;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author JoeAlisson
 */
public abstract class TradeAdd extends AbstractItemPacket {

    private final int type;
    private final TradeItem item;

    protected TradeAdd(int type, TradeItem item) {
        this.type = type;
        this.item = item;
    }

    protected void writeItemAdd() {
        writeByte(type);
        if (type == 2) {
            writeInt(0x01);
        }
        writeInt(0x01);
        writeItem(item);
    }
}
