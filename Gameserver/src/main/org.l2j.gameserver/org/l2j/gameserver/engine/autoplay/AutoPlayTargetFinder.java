package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
interface AutoPlayTargetFinder {

    boolean canBeTarget(Player player, WorldObject target);

    Creature findNextTarget(Player player, int range);

}
