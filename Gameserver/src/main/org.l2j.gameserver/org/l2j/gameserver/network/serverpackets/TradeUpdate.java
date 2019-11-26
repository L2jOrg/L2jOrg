package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author daemon
 */
public class TradeUpdate extends AbstractItemPacket {
    private final int sendType;
    private final TradeItem item;
    private final long newCount;
    private final long count;

    public TradeUpdate(int sendType, Player player, TradeItem item, long count) {
        this.sendType = sendType;
        this.count = count;
        this.item = item;
        newCount = player == null ? 0 : player.getInventory().getItemByObjectId(item.getObjectId()).getCount() - item.getCount();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TRADE_UPDATE);
        writeByte((byte) sendType);
        writeInt(0x01);
        if (sendType == 2) {
            writeInt(0x01);
            writeShort((short) ((newCount > 0) && item.getItem().isStackable() ? 3 : 2));
            writeItem(item, count);
        }
    }

}
