package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Kerberos
 */
public class AcquireSkillDone extends IClientOutgoingPacket {

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.ACQUIRE_SKILL_DONE);
    }

}