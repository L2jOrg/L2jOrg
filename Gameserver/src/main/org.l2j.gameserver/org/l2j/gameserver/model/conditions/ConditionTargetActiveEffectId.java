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
import org.l2j.gameserver.model.skills.BuffInfo;

/**
 * The Class ConditionTargetActiveEffectId.
 */
public class ConditionTargetActiveEffectId extends Condition {
    private final int _effectId;
    private final int _effectLvl;

    /**
     * Instantiates a new condition target active effect id.
     *
     * @param effectId the effect id
     */
    public ConditionTargetActiveEffectId(int effectId) {
        _effectId = effectId;
        _effectLvl = -1;
    }

    /**
     * Instantiates a new condition target active effect id.
     *
     * @param effectId    the effect id
     * @param effectLevel the effect level
     */
    public ConditionTargetActiveEffectId(int effectId, int effectLevel) {
        _effectId = effectId;
        _effectLvl = effectLevel;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final BuffInfo info = effected.getEffectList().getBuffInfoBySkillId(_effectId);
        if ((info != null) && ((_effectLvl == -1) || (_effectLvl <= info.getSkill().getLevel()))) {
            return true;
        }
        return false;
    }
}
