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
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionTargetAggro.
 *
 * @author mkizub
 */
public class ConditionTargetAggro extends Condition {
    private final boolean _isAggro;

    /**
     * Instantiates a new condition target aggro.
     *
     * @param isAggro the is aggro
     */
    public ConditionTargetAggro(boolean isAggro) {
        _isAggro = isAggro;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (isMonster(effected)) {
            return ((Monster) effected).isAggressive() == _isAggro;
        }
        if (isPlayer(effected)) {
            return effected.getReputation() < 0;
        }
        return false;
    }
}
