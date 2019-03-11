package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExNewSkillToLearnByLevelUp extends IClientOutgoingPacket {
    public static final ExNewSkillToLearnByLevelUp STATIC_PACKET = new ExNewSkillToLearnByLevelUp();

    private ExNewSkillToLearnByLevelUp() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NEW_SKILL_TO_LEARN_BY_LEVEL_UP.writeId(packet);
    }
}
