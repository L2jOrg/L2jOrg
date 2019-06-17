package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExInzoneWaiting;

/**
 * @author UnAfraid
 */
public class RequestInzoneWaitingTime extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        client.sendPacket(new ExInzoneWaiting(activeChar, true));
    }
}
