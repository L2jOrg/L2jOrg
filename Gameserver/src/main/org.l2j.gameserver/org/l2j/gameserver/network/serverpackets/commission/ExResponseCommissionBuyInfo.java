package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_RESPONSE_COMMISSION_BUY_INFO);

        writeInt(_commissionItem != null ? 1 : 0);
        if (_commissionItem != null) {
            writeLong(_commissionItem.getPricePerUnit());
            writeLong(_commissionItem.getCommissionId());
            writeInt(0); // CommissionItemType seems client does not really need it.
            writeItem(_commissionItem.getItemInfo());
        }
    }

}
