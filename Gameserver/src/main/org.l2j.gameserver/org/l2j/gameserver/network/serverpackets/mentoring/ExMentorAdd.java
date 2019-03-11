package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public class ExMentorAdd extends IClientOutgoingPacket {
    final L2PcInstance _mentor;

    public ExMentorAdd(L2PcInstance mentor) {
        _mentor = mentor;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_MENTOR_ADD.writeId(packet);

        writeString(_mentor.getName(), packet);
        packet.putInt(_mentor.getActiveClass());
        packet.putInt(_mentor.getLevel());
    }
}
