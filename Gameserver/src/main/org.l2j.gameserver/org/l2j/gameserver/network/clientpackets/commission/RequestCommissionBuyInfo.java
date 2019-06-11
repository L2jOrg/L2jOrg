package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.commission.CommissionItem;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionBuyInfo;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class RequestCommissionBuyInfo extends IClientIncomingPacket {
    private long _commissionId;

    @Override
    public void readImpl() {
        _commissionId = readLong();
        // readInt(); // CommissionItemType
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            client.sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }

        if (!player.isInventoryUnder80(false) || (player.getWeightPenalty() >= 3)) {
            client.sendPacket(SystemMessageId.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE);
            client.sendPacket(ExResponseCommissionBuyInfo.FAILED);
            return;
        }

        final CommissionItem commissionItem = CommissionManager.getInstance().getCommissionItem(_commissionId);
        if (commissionItem != null) {
            client.sendPacket(new ExResponseCommissionBuyInfo(commissionItem));
        } else {
            client.sendPacket(SystemMessageId.ITEM_PURCHASE_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST);
            client.sendPacket(ExResponseCommissionBuyInfo.FAILED);
        }
    }
}
