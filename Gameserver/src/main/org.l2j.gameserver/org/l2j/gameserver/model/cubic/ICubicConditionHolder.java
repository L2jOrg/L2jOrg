package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;

/**
 * @author UnAfraid
 */
public interface ICubicConditionHolder {
    boolean validateConditions(CubicInstance cubic, Creature owner, L2Object target);

    void addCondition(ICubicCondition condition);
}
