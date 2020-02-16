package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerMinLevel.
 *
 * @author mkizub
 */
public class ConditionPlayerMinLevel extends Condition {
    public final int _level;

    /**
     * Instantiates a new condition player level.
     *
     * @param level the level
     */
    public ConditionPlayerMinLevel(int level) {
        _level = level;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return effector.getLevel() >= _level;
    }
}
