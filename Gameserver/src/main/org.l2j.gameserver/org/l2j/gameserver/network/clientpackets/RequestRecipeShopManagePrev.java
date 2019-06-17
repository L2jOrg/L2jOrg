package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.RecipeShopSellList;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRecipeShopManagePrev extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null)) {
            return;
        } else if (player.isAlikeDead() || (player.getTarget() == null) || !player.getTarget().isPlayer()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.sendPacket(new RecipeShopSellList(player, player.getTarget().getActingPlayer()));
    }
}
