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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerActiveEffectId.
 */
public class ConditionPlayerActiveEffectId extends Condition {

    private final int _effectId;
    private final int _effectLvl;

    /**
     * Instantiates a new condition player active effect id.
     *
     * @param effectId the effect id
     */
    public ConditionPlayerActiveEffectId(int effectId) {
        _effectId = effectId;
        _effectLvl = -1;
    }

    /**
     * Instantiates a new condition player active effect id.
     *
     * @param effectId    the effect id
     * @param effectLevel the effect level
     */
    public ConditionPlayerActiveEffectId(int effectId, int effectLevel) {
        _effectId = effectId;
        _effectLvl = effectLevel;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final BuffInfo info = effector.getEffectList().getBuffInfoBySkillId(_effectId);
        return ((info != null) && ((_effectLvl == -1) || (_effectLvl <= info.getSkill().getLevel())));
    }
}
