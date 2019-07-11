package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;

/**
 * @author UnAfraid
 */
public interface ICubicCondition {
    boolean test(CubicInstance cubic, Creature owner, L2Object target);
}
