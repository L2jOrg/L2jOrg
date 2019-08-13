package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * A simple no summon zone
 *
 * @author JIV
 */
public class NoSummonFriendZone extends Zone {
    public NoSummonFriendZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);
    }
}
