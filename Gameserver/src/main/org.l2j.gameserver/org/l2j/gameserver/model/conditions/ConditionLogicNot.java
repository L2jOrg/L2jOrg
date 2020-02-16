package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionLogicNot.
 *
 * @author mkizub
 */
public class ConditionLogicNot extends Condition {
    public final Condition _condition;

    /**
     * Instantiates a new condition logic not.
     *
     * @param condition the condition
     */
    public ConditionLogicNot(Condition condition) {
        _condition = condition;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return !_condition.test(effector, effected, skill, item);
    }
}
