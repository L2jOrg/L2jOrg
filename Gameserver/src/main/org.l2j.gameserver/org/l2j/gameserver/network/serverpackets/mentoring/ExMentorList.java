package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.L2Mentee;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author UnAfraid
 */
public class ExMentorList extends IClientOutgoingPacket {
    private final int _type;
    private final Collection<L2Mentee> _mentees;

    public ExMentorList(L2PcInstance activeChar) {
        if (activeChar.isMentor()) {
            _type = 0x01;
            _mentees = MentorManager.getInstance().getMentees(activeChar.getObjectId());
        } else if (activeChar.isMentee()) {
            _type = 0x02;
            _mentees = Arrays.asList(MentorManager.getInstance().getMentor(activeChar.getObjectId()));
        } else if (activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP)) // Not a mentor, Not a mentee, so can be a mentor
        {
            _mentees = Collections.emptyList();
            _type = 0x01;
        } else {
            _mentees = Collections.emptyList();
            _type = 0x00;
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MENTOR_LIST.writeId(packet);

        packet.putInt(_type);
        packet.putInt(0x00);
        packet.putInt(_mentees.size());
        for (L2Mentee mentee : _mentees) {
            packet.putInt(mentee.getObjectId());
            writeString(mentee.getName(), packet);
            packet.putInt(mentee.getClassId());
            packet.putInt(mentee.getLevel());
            packet.putInt(mentee.isOnlineInt());
        }
    }
}
