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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

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
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (isPlayer(effected)) {
            final Player target = effected.getActingPlayer();
            if (!target.getDietMode() && (target.getMaxLoad() > 0)) {
                final int weightproc = (((target.getCurrentLoad() - target.getBonusWeightPenalty()) * 100) / target.getMaxLoad());
                return (weightproc < _weight);
            }
        }
        return false;
    }
}
