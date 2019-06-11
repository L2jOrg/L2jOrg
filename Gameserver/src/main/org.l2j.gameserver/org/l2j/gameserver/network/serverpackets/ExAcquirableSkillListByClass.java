package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS);

        writeShort((short) _type.getId());
        writeShort((short) _learnable.size());
        for (L2SkillLearn skill : _learnable) {
            writeInt(skill.getSkillId());
            writeShort((short) skill.getSkillLevel());
            writeShort((short) skill.getSkillLevel());
            writeByte((byte) skill.getGetLevel());
            writeLong(skill.getLevelUpSp());
            writeByte((byte) skill.getRequiredItems().size());
            if (_type == AcquireSkillType.SUBPLEDGE) {
                writeShort((short) 0x00);
            }
        }
    }

}
