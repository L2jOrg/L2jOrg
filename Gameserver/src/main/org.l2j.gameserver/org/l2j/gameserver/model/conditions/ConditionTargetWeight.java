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

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * The Class ConditionTargetWeight.
 *
 * @author Zoey76
 */
public class ConditionTargetWeight extends Condition {
    private final int _weight;

    /**
     * Instantiates a new condition player weight.
     *
     * @param weight the weight
     */
    public ConditionTargetWeight(int weight) {
        _weight = weight;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if ((effected != null) && effected.isPlayer()) {
            final L2PcInstance target = effected.getActingPlayer();
            if (!target.getDietMode() && (target.getMaxLoad() > 0)) {
                final int weightproc = (((target.getCurrentLoad() - target.getBonusWeightPenalty()) * 100) / target.getMaxLoad());
                return (weightproc < _weight);
            }
        }
        return false;
    }
}
