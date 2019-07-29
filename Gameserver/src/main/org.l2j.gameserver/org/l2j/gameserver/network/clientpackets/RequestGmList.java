package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * This class handles RequestGmLista packet triggered by /gmlist command
 *
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestGmList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        AdminData.getInstance().sendListToPlayer(activeChar);
    }
}
