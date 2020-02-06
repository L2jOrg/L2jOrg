package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.util.GameUtils;

/**
 * Player Can Escape condition implementation.
 *
 * @author Adry_85
 * @author joeAlisson
 */
public class ConditionPlayerCanEscape extends Condition {
    private final boolean val;

    public ConditionPlayerCanEscape(boolean val) {
        this.val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return (val == GameUtils.canTeleport( effector.getActingPlayer() ));
    }
}