package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExAlterSkillRequest extends IClientOutgoingPacket {
    private final int _currentSkillId;
    private final int _nextSkillId;
    private final int _alterTime;

    public ExAlterSkillRequest(int currentSkill, int nextSkill, int alterTime) {
        _currentSkillId = currentSkill;
        _nextSkillId = nextSkill;
        _alterTime = alterTime;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ALTER_SKILL_REQUEST.writeId(packet);
        packet.putInt(_nextSkillId);
        packet.putInt(_currentSkillId);
        packet.putInt(_alterTime);
    }
}
