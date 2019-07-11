package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;

/**
 * @author UnAfraid
 */
public interface ICubicConditionHolder {
    boolean validateConditions(CubicInstance cubic, Creature owner, WorldObject target);

    void addCondition(ICubicCondition condition);
}
