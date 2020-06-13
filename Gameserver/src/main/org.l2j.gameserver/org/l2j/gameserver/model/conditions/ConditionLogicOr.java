/*
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
 * The Class ConditionLogicOr.
 *
 * @author mkizub
 */
public class ConditionLogicOr extends Condition {
    private static Condition[] _emptyConditions = new Condition[0];
    public Condition[] conditions = _emptyConditions;

    /**
     * Adds the.
     *
     * @param condition the condition
     */
    public void add(Condition condition) {
        if (condition == null) {
            return;
        }
        final int len = conditions.length;
        final Condition[] tmp = new Condition[len + 1];
        System.arraycopy(conditions, 0, tmp, 0, len);
        tmp[len] = condition;
        conditions = tmp;
    }


    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        for (Condition c : conditions) {
            if (c.test(effector, effected, skill, item)) {
                return true;
            }
        }
        return false;
    }
}
