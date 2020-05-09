package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
class MonsterFinder extends AbstractAutoPlayTargetFinder {

    @Override
    public boolean canBeTarget(Player player, WorldObject target) {
        return  target instanceof Monster monster && !monster.isDead() && super.canBeTarget(player, monster);
    }

    @Override
    public Creature findNextTarget(Player player, int range) {
        return findNextTarget(player, Monster.class, range);
    }
}
