package org.l2j.gameserver.mobius.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.mentoring.ExMentorList;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestMentorList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
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
