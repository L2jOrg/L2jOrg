package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

/**
 * @author Sdw
 */
public interface ICondition {
    boolean test(Creature creature, WorldObject object);
}
