package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExNewSkillToLearnByLevelUp extends ServerPacket {
    public static final ExNewSkillToLearnByLevelUp STATIC_PACKET = new ExNewSkillToLearnByLevelUp();

    private ExNewSkillToLearnByLevelUp() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_NEW_SKILL_TO_LEARN_BY_LEVEL_UP);
    }

}
