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
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SkillList extends ServerPacket {
    private final List<Skill> _skills = new ArrayList<>();
    private int _lastLearnedSkillId = 0;

    public void addSkill(int id, int reuseDelayGroup, int level, int subLevel, boolean passive, boolean disabled, boolean enchanted) {
        _skills.add(new Skill(id, reuseDelayGroup, level, subLevel, passive, disabled, enchanted));
    }

    public void setLastLearnedSkillId(int lastLearnedSkillId) {
        _lastLearnedSkillId = lastLearnedSkillId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SKILL_LIST, buffer );
        _skills.sort(Comparator.comparing(s -> SkillEngine.getInstance().getSkill(s.id, s.level).isToggle() ? 1 : 0));
        buffer.writeInt(_skills.size());
        for (Skill temp : _skills) {
            buffer.writeInt(temp.passive ? 1 : 0);
            buffer.writeShort(temp.level);
            buffer.writeShort(temp.subLevel);
            buffer.writeInt(temp.id);
            buffer.writeInt(temp.reuseDelayGroup); // GOD ReuseDelayShareGroupID
            buffer.writeByte(temp.disabled); // iSkillDisabled
            buffer.writeByte(temp.enchanted); // CanEnchant
        }
        buffer.writeInt(_lastLearnedSkillId);
    }


    static class Skill {
        public int id;
        public int reuseDelayGroup;
        public int level;
        public int subLevel;
        public boolean passive;
        public boolean disabled;
        public boolean enchanted;

        Skill(int pId, int pReuseDelayGroup, int pLevel, int pSubLevel, boolean pPassive, boolean pDisabled, boolean pEnchanted) {
            id = pId;
            reuseDelayGroup = pReuseDelayGroup;
            level = pLevel;
            subLevel = pSubLevel;
            passive = pPassive;
            disabled = pDisabled;
            enchanted = pEnchanted;
        }
    }
}
