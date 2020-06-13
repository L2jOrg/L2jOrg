/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;

/**
 * The Class ConditionPlayerActiveSkillId.
 *
 * @author DrHouse
 */
public class ConditionPlayerActiveSkillId extends Condition {
    private final int _skillId;
    private final int _skillLevel;

    /**
     * Instantiates a new condition player active skill id.
     *
     * @param skillId the skill id
     */
    public ConditionPlayerActiveSkillId(int skillId) {
        _skillId = skillId;
        _skillLevel = -1;
    }

    /**
     * Instantiates a new condition player active skill id.
     *
     * @param skillId    the skill id
     * @param skillLevel the skill level
     */
    public ConditionPlayerActiveSkillId(int skillId, int skillLevel) {
        _skillId = skillId;
        _skillLevel = skillLevel;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final Skill knownSkill = effector.getKnownSkill(_skillId);
        if (knownSkill != null) {
            return (_skillLevel == -1) || (_skillLevel <= knownSkill.getLevel());
        }

        return false;
    }
}
