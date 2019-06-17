package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ShowMiniMap;

/**
 * sample format d
 *
 * @version $Revision: 1 $ $Date: 2005/04/10 00:17:44 $
 */
public final class RequestShowMiniMap extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        client.sendPacket(new ShowMiniMap(0));
    }
}
