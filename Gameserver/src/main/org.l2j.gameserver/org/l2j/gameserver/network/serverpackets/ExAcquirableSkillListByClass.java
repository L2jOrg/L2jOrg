/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

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
        writeId(ServerExPacketId.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS);

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
