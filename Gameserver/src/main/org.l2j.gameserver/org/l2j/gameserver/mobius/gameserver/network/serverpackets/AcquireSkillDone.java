package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Kerberos
 */
public class AcquireSkillDone extends IClientOutgoingPacket {
    public AcquireSkillDone() {
        //
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ACQUIRE_SKILL_DONE.writeId(packet);
    }
}