package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Kerberos
 */
public class AcquireSkillDone extends IClientOutgoingPacket {

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ACQUIRE_SKILL_DONE.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}