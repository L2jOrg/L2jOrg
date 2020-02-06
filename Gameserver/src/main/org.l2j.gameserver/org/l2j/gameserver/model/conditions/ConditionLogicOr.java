package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

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
