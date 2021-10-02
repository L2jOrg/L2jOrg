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
package org.l2j.gameserver.network.serverpackets.skill;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ExAcquireSkillInfo extends ServerPacket {

    private final SkillLearn skillLearn;

    /**
     * Special constructor for Alternate Skill Learning system.<br>
     * Sets a custom amount of SP.
     *
     * @param skillLearn the skill learn.
     */
    public ExAcquireSkillInfo(SkillLearn skillLearn) {
        this.skillLearn = skillLearn;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_ACQUIRE_SKILL_INFO, buffer );
        buffer.writeInt(skillLearn.getSkillId());
        buffer.writeInt(skillLearn.getSkillLevel());
        buffer.writeLong(skillLearn.getLevelUpSp());
        buffer.writeShort(skillLearn.requiredLevel());
        buffer.writeShort(0x00); // dual class level

        buffer.writeInt(skillLearn.getRequiredItems().size());
        for (var item : skillLearn.getRequiredItems()) {
            buffer.writeInt(item.getId());
            buffer.writeLong(item.getCount());
        }

        buffer.writeInt(skillLearn.getReplacedSkills().size());
        for (var skill : skillLearn.getReplacedSkills()) {
            buffer.writeInt(skill.getId());
            buffer.writeInt(skill.getLevel());
        }
    }
}
