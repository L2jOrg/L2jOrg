package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.mobius.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExAcquirableSkillListByClass extends IClientOutgoingPacket {
    final List<L2SkillLearn> _learnable;
    final AcquireSkillType _type;

    public ExAcquirableSkillListByClass(List<L2SkillLearn> learnable, AcquireSkillType type) {
        _learnable = learnable;
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS.writeId(packet);

        packet.putShort((short) _type.getId());
        packet.putShort((short) _learnable.size());
        for (L2SkillLearn skill : _learnable) {
            packet.putInt(skill.getSkillId());
            packet.putShort((short) skill.getSkillLevel());
            packet.putShort((short) skill.getSkillLevel());
            packet.put((byte) skill.getGetLevel());
            packet.putLong(skill.getLevelUpSp());
            packet.put((byte) skill.getRequiredItems().size());
            if (_type == AcquireSkillType.SUBPLEDGE) {
                packet.putShort((short) 0x00);
            }
        }
    }
}
