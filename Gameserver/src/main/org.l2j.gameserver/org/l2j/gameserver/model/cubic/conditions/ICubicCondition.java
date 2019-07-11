package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;

/**
 * @author UnAfraid
 */
public interface ICubicCondition {
    boolean test(CubicInstance cubic, Creature owner, WorldObject target);
}
