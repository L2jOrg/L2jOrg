package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author UnAfraid
 */
public class ExAcquirableSkillListByClass extends ServerPacket {
    final List<SkillLearn> _learnable;
    final AcquireSkillType _type;

    public ExAcquirableSkillListByClass(List<SkillLearn> learnable, AcquireSkillType type) {
        _learnable = learnable;
        _type = type;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS);

        writeShort((short) _type.getId());
        writeShort((short) _learnable.size());
        for (SkillLearn skill : _learnable) {
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
