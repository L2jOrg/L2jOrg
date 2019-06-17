package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Kerberos
 */
public class AcquireSkillDone extends ServerPacket {

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.ACQUIRE_SKILL_DONE);
    }

}