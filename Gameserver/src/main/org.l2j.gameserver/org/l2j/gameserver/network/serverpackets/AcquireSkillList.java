/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Sdw, Mobius
 * @author JoeAlisson
 */
public class AcquireSkillList extends ServerPacket {
    final List<SkillLearn> skills;

    public AcquireSkillList(Player player) {
        skills = SkillTreesData.getInstance().getAvailableSkills(player, player.getClassId(), false);
        skills.addAll(SkillTreesData.getInstance().getNextAvailableSkills(player, player.getClassId(), false));
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.ACQUIRE_SKILL_LIST, buffer );

        buffer.writeShort(skills.size());
        for (SkillLearn skill : skills) {
            if (skill == null) {
                continue;
            }
            buffer.writeInt(skill.getSkillId());
            buffer.writeShort(skill.getSkillLevel());
            buffer.writeLong(skill.getLevelUpSp());
            buffer.writeByte(skill.requiredLevel());
            buffer.writeByte(0x00); // dual class level requirement
            buffer.writeByte(skill.getSkillLevel() == 1);// new skill

            buffer.writeByte(skill.getRequiredItems().size());
            for (var requiredItem : skill.getRequiredItems()) {
                buffer.writeInt(requiredItem.getId());
                buffer.writeLong(requiredItem.getCount());
            }

            buffer.writeByte(0x00); // skills to be removed
        }
    }

}
