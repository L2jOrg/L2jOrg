package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionItemList;

/**
 * @author NosBit
 */
public class RequestCommissionRegistrableItemList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            client.sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }

        client.sendPacket(new ExResponseCommissionItemList(1, player.getInventory().getAvailableItems(false, false, false)));
        client.sendPacket(new ExResponseCommissionItemList(2, player.getInventory().getAvailableItems(false, false, false)));
    }
}
