package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This class ... cd(dd)
 *
 * @version $Revision: 1.1.2.2.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRecipeShopManageQuit extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        player.setPrivateStoreType(PrivateStoreType.NONE);
        player.broadcastUserInfo();
        player.standUp();
    }
}
