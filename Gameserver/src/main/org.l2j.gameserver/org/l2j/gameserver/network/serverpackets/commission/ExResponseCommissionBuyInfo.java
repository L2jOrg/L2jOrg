package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_BUY_INFO);

        writeInt(_commissionItem != null ? 1 : 0);
        if (_commissionItem != null) {
            writeLong(_commissionItem.getPricePerUnit());
            writeLong(_commissionItem.getCommissionId());
            writeInt(0); // CommissionItemType seems client does not really need it.
            writeItem(_commissionItem.getItemInfo());
        }
    }

}
