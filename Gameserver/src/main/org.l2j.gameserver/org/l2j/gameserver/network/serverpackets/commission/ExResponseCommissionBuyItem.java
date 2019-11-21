package org.l2j.gameserver.network.serverpackets.commission;

import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author NosBit
 */
public class ExResponseCommissionBuyItem extends ServerPacket {
    public static final ExResponseCommissionBuyItem FAILED = new ExResponseCommissionBuyItem(null);

    private final CommissionItem _commissionItem;

    public ExResponseCommissionBuyItem(CommissionItem commissionItem) {
        _commissionItem = commissionItem;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_RESPONSE_COMMISSION_BUY_ITEM);

        writeInt(_commissionItem != null ? 1 : 0);
        if (_commissionItem != null) {
            final ItemInfo itemInfo = _commissionItem.getItemInfo();
            writeInt(itemInfo.getEnchantLevel());
            writeInt(itemInfo.getId());
            writeLong(itemInfo.getCount());
        }
    }

}
