package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.ItemTemplate;

import static java.util.Objects.isNull;

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
        final var player = effector.getActingPlayer();
        if (isNull(player) || isNull(player.getClan())) {
            return false;
        }
        return player.isClanLeader() || (_pledgeClass != -1 && player.getPledgeClass() >= _pledgeClass);
    }
}
