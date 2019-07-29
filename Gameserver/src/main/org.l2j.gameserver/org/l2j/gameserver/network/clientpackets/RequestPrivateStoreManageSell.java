package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestPrivateStoreManageSell extends ClientPacket {
    @Override
    public void readImpl() {
        // TODO: implement me properly
        // readInt();
        // readLong();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        // Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
        if (player.isAlikeDead() || player.isInOlympiadMode()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
    }
}
