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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;

import static java.util.Objects.isNull;

/**
 * Simple class for storing skill id/level.
 *
 * @author BiggBoss
 */
public class SkillHolder {
    private final int skillId;
    private final int skillLevel;
    private Skill skill;

    public SkillHolder(int skillId, int skillLevel) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
    }

    public final int getSkillId() {
        return skillId;
    }

    public final int getLevel() {
        return skillLevel;
    }


    public final Skill getSkill() {
        if(isNull(skill)) {
            skill = SkillEngine.getInstance().getSkill(skillId, Math.max(skillLevel, 1));
        }
        return skill;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SkillHolder holder)) {
            return false;
        }
        return holder.getSkillId() == skillId && holder.getLevel() == skillLevel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + skillId;
        result = (prime * result) + skillLevel;
        return result;
    }

    @Override
    public String toString() {
        return "[SkillId: " + skillId + " Level: " + skillLevel + "]";
    }
}