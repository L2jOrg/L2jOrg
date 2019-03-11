package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.RecipeShopManageList;

import java.nio.ByteBuffer;

public final class RequestRecipeShopManageList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
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
