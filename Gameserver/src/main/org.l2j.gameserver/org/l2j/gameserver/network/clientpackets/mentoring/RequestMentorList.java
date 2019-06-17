package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.mentoring.ExMentorList;

/**
 * @author UnAfraid
 */
public class RequestMentorList extends ClientPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        client.sendPacket(new ExMentorList(activeChar));
    }
}
