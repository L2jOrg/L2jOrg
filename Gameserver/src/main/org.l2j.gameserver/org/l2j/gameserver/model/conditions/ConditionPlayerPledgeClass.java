package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerPledgeClass.
 *
 * @author MrPoke
 */
public final class ConditionPlayerPledgeClass extends Condition {

    public final int _pledgeClass;

    /**
     * Instantiates a new condition player pledge class.
     *
     * @param pledgeClass the pledge class
     */
    public ConditionPlayerPledgeClass(int pledgeClass) {
        _pledgeClass = pledgeClass;
    }

    /**
     * Test impl.
     *
     * @return true, if successful
     */
    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if ((effector.getActingPlayer() == null) || (effector.getActingPlayer().getClan() == null)) {
            return false;
        }
        return (_pledgeClass == -1) ? effector.getActingPlayer().isClanLeader() : (effector.getActingPlayer().getPledgeClass() >= _pledgeClass);
    }
}
