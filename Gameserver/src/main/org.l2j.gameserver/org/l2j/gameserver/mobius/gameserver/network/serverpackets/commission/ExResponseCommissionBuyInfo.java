package org.l2j.gameserver.mobius.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.mobius.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AbstractItemPacket;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class ExResponseCommissionBuyInfo extends AbstractItemPacket {
    public static final ExResponseCommissionBuyInfo FAILED = new ExResponseCommissionBuyInfo(null);

    private final CommissionItem _commissionItem;

    public ExResponseCommissionBuyInfo(CommissionItem commissionItem) {
        _commissionItem = commissionItem;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RESPONSE_COMMISSION_BUY_INFO.writeId(packet);

        packet.putInt(_commissionItem != null ? 1 : 0);
        if (_commissionItem != null) {
            packet.putLong(_commissionItem.getPricePerUnit());
            packet.putLong(_commissionItem.getCommissionId());
            packet.putInt(0); // CommissionItemType seems client does not really need it.
            writeItem(packet, _commissionItem.getItemInfo());
        }
    }
}
