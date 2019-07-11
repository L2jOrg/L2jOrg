package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gnacik, UnAfraid
 */
public class ExMentorAdd extends ServerPacket {
    final Player _mentor;

    public ExMentorAdd(Player mentor) {
        _mentor = mentor;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_MENTOR_ADD);

        writeString(_mentor.getName());
        writeInt(_mentor.getActiveClass());
        writeInt(_mentor.getLevel());
    }

}
