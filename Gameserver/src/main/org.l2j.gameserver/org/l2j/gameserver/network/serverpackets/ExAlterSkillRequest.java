package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author UnAfraid
 */
public class ExAlterSkillRequest extends ServerPacket {
    private final int _currentSkillId;
    private final int _nextSkillId;
    private final int _alterTime;

    public ExAlterSkillRequest(int currentSkill, int nextSkill, int alterTime) {
        _currentSkillId = currentSkill;
        _nextSkillId = nextSkill;
        _alterTime = alterTime;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ALTER_SKILL_REQUEST);
        writeInt(_nextSkillId);
        writeInt(_currentSkillId);
        writeInt(_alterTime);
    }

}
