package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestPrivateStoreQuitBuy extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        player.setPrivateStoreType(PrivateStoreType.NONE);
        player.standUp();
        player.broadcastUserInfo();
    }
}
