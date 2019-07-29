package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.RecipeShopSellList;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

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
        final Player player = client.getPlayer();
        if ((player == null)) {
            return;
        } else if (player.isAlikeDead() || !isPlayer(player.getTarget())) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        player.sendPacket(new RecipeShopSellList(player, player.getTarget().getActingPlayer()));
    }
}
