package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author NosBit
 */
public class ConditionUsingSlotType extends Condition {
    private final long _mask;

    public ConditionUsingSlotType(long mask) {
        _mask = mask;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return false;
        }

        return (effector.getActiveWeaponItem().getBodyPart().getId() & _mask) != 0;
    }

}
