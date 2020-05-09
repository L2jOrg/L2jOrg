package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.FriendlyMob;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
class FriendlyMobFinder extends AbstractAutoPlayTargetFinder {

    @Override
    public boolean canBeTarget(Player player, WorldObject target) {
        return target instanceof FriendlyMob friendly && !friendly.isDead() && super.canBeTarget(player, friendly);
    }

    @Override
    public Creature findNextTarget(Player player, int range) {
        return findNextTarget(player, FriendlyMob.class, range);
    }
}
