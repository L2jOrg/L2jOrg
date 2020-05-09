package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
class TauntFinder extends AbstractAutoPlayTargetFinder {

    @Override
    public boolean canBeTarget(Player player, WorldObject target) {
        return target instanceof Creature creature && !creature.isDead() && super.canBeTarget(player, creature);
    }

    @Override
    public Creature findNextTarget(Player player, int range) {
        return findNextTarget(player, Creature.class, range);
    }
}
