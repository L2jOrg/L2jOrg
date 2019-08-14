package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * An Undying Zone
 *
 * @author UnAfraid
 */
public class UndyingZone extends Zone {
    public UndyingZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.UNDYING, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.UNDYING, false);
    }
}
