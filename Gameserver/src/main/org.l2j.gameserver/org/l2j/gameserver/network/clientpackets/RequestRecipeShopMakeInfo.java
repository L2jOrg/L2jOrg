package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.RecipeShopItemInfo;

import java.nio.ByteBuffer;

/**
 * This class ... cdd
 *
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRecipeShopMakeInfo extends IClientIncomingPacket {
    private int _playerObjectId;
    private int _recipeId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _playerObjectId = packet.getInt();
        _recipeId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final L2PcInstance shop = L2World.getInstance().getPlayer(_playerObjectId);
        if ((shop == null) || (shop.getPrivateStoreType() != PrivateStoreType.MANUFACTURE)) {
            return;
        }

        client.sendPacket(new RecipeShopItemInfo(shop, _recipeId));
    }
}
