package org.l2j.gameserver.mobius.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.mobius.gameserver.model.ItemInfo;
import org.l2j.gameserver.mobius.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class ExResponseCommissionBuyItem extends IClientOutgoingPacket {
    public static final ExResponseCommissionBuyItem FAILED = new ExResponseCommissionBuyItem(null);

    private final CommissionItem _commissionItem;

    public ExResponseCommissionBuyItem(CommissionItem commissionItem) {
        _commissionItem = commissionItem;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RESPONSE_COMMISSION_BUY_ITEM.writeId(packet);

        packet.putInt(_commissionItem != null ? 1 : 0);
        if (_commissionItem != null) {
            final ItemInfo itemInfo = _commissionItem.getItemInfo();
            packet.putInt(itemInfo.getEnchantLevel());
            packet.putInt(itemInfo.getItem().getId());
            packet.putLong(itemInfo.getCount());
        }
    }
}
