package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class ExSubPledgeSkillAdd extends IClientOutgoingPacket {
    private final int _type;
    private final int _skillId;
    private final int _skillLevel;

    public ExSubPledgeSkillAdd(int type, int skillId, int skillLevel) {
        _type = type;
        _skillId = skillId;
        _skillLevel = skillLevel;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SUB_PLEDGET_SKILL_ADD.writeId(packet);

        packet.putInt(_type);
        packet.putInt(_skillId);
        packet.putInt(_skillLevel);
    }
}
