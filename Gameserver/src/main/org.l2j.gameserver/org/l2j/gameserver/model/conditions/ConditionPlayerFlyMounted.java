package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerFlyMounted.
 *
 * @author kerberos
 * @author JoeAlisson
 */
public class ConditionPlayerFlyMounted extends Condition {

    private static final ConditionPlayerFlyMounted FLYING = new ConditionPlayerFlyMounted(true);
    private static final ConditionPlayerFlyMounted NO_FLYING = new ConditionPlayerFlyMounted(false);

    public final boolean _val;

    /**
     * Instantiates a new condition player fly mounted.
     *
     * @param val the val
     */
    private ConditionPlayerFlyMounted(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return (effector.getActingPlayer() == null) || (effector.getActingPlayer().isFlyingMounted() == _val);
    }

    public static ConditionPlayerFlyMounted of(boolean flying) {
        return flying ? FLYING : NO_FLYING;
    }
}
