package org.l2j.gameserver.mobius.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.commission.ExCloseCommission;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class RequestCommissionRegisteredItem extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
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

        CommissionManager.getInstance().showPlayerAuctions(player);
    }
}
