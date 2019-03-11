package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class RequestCommissionDelete extends IClientIncomingPacket {
    private long _commissionId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _commissionId = packet.getLong();
        // packet.getInt(); // CommissionItemType
        // packet.getInt(); // CommissionDurationType
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

        CommissionManager.getInstance().deleteItem(player, _commissionId);
    }
}
