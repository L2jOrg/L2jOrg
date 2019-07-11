package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.RecipeShopItemInfo;

/**
 * This class ... cdd
 *
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRecipeShopMakeInfo extends ClientPacket {
    private int _playerObjectId;
    private int _recipeId;

    @Override
    public void readImpl() {
        _playerObjectId = readInt();
        _recipeId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final Player shop = World.getInstance().getPlayer(_playerObjectId);
        if ((shop == null) || (shop.getPrivateStoreType() != PrivateStoreType.MANUFACTURE)) {
            return;
        }

        client.sendPacket(new RecipeShopItemInfo(shop, _recipeId));
    }
}
