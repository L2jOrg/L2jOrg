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
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * Condition implementation to verify player's abnormal type and level.
 *
 * @author Zoey76
 */
public class ConditionPlayerCheckAbnormal extends Condition {
    private final AbnormalType _type;
    private final int _level;

    /**
     * Instantiates a new condition player check abnormal.
     *
     * @param type the abnormal type
     */
    public ConditionPlayerCheckAbnormal(AbnormalType type) {
        _type = type;
        _level = -1;
    }

    /**
     * Instantiates a new condition player check abnormal.
     *
     * @param type  the abnormal type
     * @param level the abnormal level
     */
    public ConditionPlayerCheckAbnormal(AbnormalType type, int level) {
        _type = type;
        _level = level;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if (_level == -1) {
            return effector.getEffectList().hasAbnormalType(_type);
        }

        return effector.getEffectList().hasAbnormalType(_type, info -> _level >= info.getSkill().getAbnormalLvl());
    }
}
