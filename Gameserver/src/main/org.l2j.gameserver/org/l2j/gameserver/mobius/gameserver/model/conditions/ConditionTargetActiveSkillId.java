/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.conditions;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * The Class ConditionTargetActiveSkillId.
 */
public class ConditionTargetActiveSkillId extends Condition {
    private final int _skillId;
    private final int _skillLevel;

    /**
     * Instantiates a new condition target active skill id.
     *
     * @param skillId the skill id
     */
    public ConditionTargetActiveSkillId(int skillId) {
        _skillId = skillId;
        _skillLevel = -1;
    }

    /**
     * Instantiates a new condition target active skill id.
     *
     * @param skillId    the skill id
     * @param skillLevel the skill level
     */
    public ConditionTargetActiveSkillId(int skillId, int skillLevel) {
        _skillId = skillId;
        _skillLevel = skillLevel;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        final Skill knownSkill = effected.getKnownSkill(_skillId);
        if (knownSkill != null) {
            return (_skillLevel == -1) || (_skillLevel <= knownSkill.getLevel());
        }

        return false;
    }
}
