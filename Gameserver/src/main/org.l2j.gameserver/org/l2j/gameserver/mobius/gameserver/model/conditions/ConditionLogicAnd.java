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
 * The Class ConditionLogicAnd.
 *
 * @author mkizub
 */
public class ConditionLogicAnd extends Condition {
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
        if (getListener() != null) {
            condition.setListener(this);
        }
        final int len = conditions.length;
        final Condition[] tmp = new Condition[len + 1];
        System.arraycopy(conditions, 0, tmp, 0, len);
        tmp[len] = condition;
        conditions = tmp;
    }

    @Override
    void setListener(ConditionListener listener) {
        if (listener != null) {
            for (Condition c : conditions) {
                c.setListener(this);
            }
        } else {
            for (Condition c : conditions) {
                c.setListener(null);
            }
        }
        super.setListener(listener);
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        for (Condition c : conditions) {
            if (!c.test(effector, effected, skill, item)) {
                return false;
            }
        }
        return true;
    }
}
