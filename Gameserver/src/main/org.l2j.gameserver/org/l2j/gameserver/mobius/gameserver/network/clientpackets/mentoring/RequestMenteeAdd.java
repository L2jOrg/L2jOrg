package org.l2j.gameserver.mobius.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.mentoring.ExMentorAdd;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public class RequestMenteeAdd extends IClientIncomingPacket {
    private String _target;

    @Override
    public void readImpl(ByteBuffer packet) {
        _target = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance mentor = client.getActiveChar();
        if (mentor == null) {
            return;
        }

        final L2PcInstance mentee = L2World.getInstance().getPlayer(_target);
        if (mentee == null) {
            return;
        }

        if (ConfirmMenteeAdd.validate(mentor, mentee)) {
            mentor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OFFERED_TO_BECOME_S1_S_MENTOR).addString(mentee.getName()));
            mentee.sendPacket(new ExMentorAdd(mentor));
        }
    }
}