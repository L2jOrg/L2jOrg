package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.RecipeShopManageList;

public final class RequestRecipeShopManageList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (player.isAlikeDead()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.setPrivateStoreType(PrivateStoreType.NONE);
            player.broadcastUserInfo();
            if (player.isSitting()) {
                player.standUp();
            }
        }

        client.sendPacket(new RecipeShopManageList(player, true));
    }
}
