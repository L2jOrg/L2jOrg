package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;

/**
 * @author UnAfraid
 */
public interface ICubicConditionHolder {
    boolean validateConditions(CubicInstance cubic, L2Character owner, L2Object target);

    void addCondition(ICubicCondition condition);
}
