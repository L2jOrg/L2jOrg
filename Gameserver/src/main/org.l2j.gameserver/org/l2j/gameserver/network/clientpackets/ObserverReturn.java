package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This class ...
 *
 * @version $Revision: 1.7.4.4 $ $Date: 2005/03/27 18:46:19 $
 */
public final class ObserverReturn extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (activeChar.inObserverMode()) {
            activeChar.leaveObserverMode();
            // activeChar.teleToLocation(activeChar.getObsX(), activeChar.getObsY(), activeChar.getObsZ());
        }
    }
}