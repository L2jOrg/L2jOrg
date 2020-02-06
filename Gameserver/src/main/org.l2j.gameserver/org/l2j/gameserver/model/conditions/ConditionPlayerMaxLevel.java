package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * @author JoeAlisson
 */
public class ConditionPlayerMaxLevel extends Condition {
    private final int level;

    public ConditionPlayerMaxLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return effector.getLevel() <= level;
    }
}
