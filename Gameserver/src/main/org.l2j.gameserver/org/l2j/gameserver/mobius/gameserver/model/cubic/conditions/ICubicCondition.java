package org.l2j.gameserver.mobius.gameserver.model.cubic.conditions;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.cubic.CubicInstance;

/**
 * @author UnAfraid
 */
public interface ICubicCondition {
    boolean test(CubicInstance cubic, L2Character owner, L2Object target);
}
