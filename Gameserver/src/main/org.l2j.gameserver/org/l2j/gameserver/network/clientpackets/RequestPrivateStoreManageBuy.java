package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPrivateStoreManageBuy extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player != null) {
            player.tryOpenPrivateBuyStore();
        }
    }
}
