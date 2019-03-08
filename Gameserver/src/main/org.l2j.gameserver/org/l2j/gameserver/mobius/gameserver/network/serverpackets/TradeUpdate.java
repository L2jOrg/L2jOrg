package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.TradeItem;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author daemon
 */
public class TradeUpdate extends AbstractItemPacket
{
    private final int _sendType;
    private final TradeItem _item;
    private final long _newCount;
    private final long _count;

    public TradeUpdate(int sendType, L2PcInstance player, TradeItem item, long count)
    {
        _sendType = sendType;
        _count = count;
        _item = item;
        _newCount = player == null ? 0 : player.getInventory().getItemByObjectId(item.getObjectId()).getCount() - item.getCount();
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.TRADE_UPDATE.writeId(packet);
        packet.writeC(_sendType);
        packet.writeD(0x01);
        if (_sendType == 2)
        {
            packet.writeD(0x01);
            packet.writeH((_newCount > 0) && _item.getItem().isStackable() ? 3 : 2);
            writeItem(packet, _item, _count);
        }
        return true;
    }
}
