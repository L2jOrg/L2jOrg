package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;
import java.util.Collections;

/**
 * @author UnAfraid
 */
public class ExMentorList extends ServerPacket {
    private final int _type;
    private final Collection<Mentee> _mentees;

    public ExMentorList(Player activeChar) {
        if (activeChar.isMentor()) {
            _type = 0x01;
            _mentees = MentorManager.getInstance().getMentees(activeChar.getObjectId());
        } else if (activeChar.isMentee()) {
            _type = 0x02;
            _mentees = Collections.singletonList(MentorManager.getInstance().getMentor(activeChar.getObjectId()));
        }  else {
            _mentees = Collections.emptyList();
            _type = 0x00;
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_MENTOR_LIST);

        writeInt(_type);
        writeInt(0x00);
        writeInt(_mentees.size());
        for (Mentee mentee : _mentees) {
            writeInt(mentee.getObjectId());
            writeString(mentee.getName());
            writeInt(mentee.getClassId());
            writeInt(mentee.getLevel());
            writeInt(mentee.isOnlineInt());
        }
    }

}
