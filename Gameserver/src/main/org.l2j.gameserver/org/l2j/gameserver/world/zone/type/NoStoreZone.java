package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Zone where store is not allowed.
 *
 * @author fordfrog
 */
public class NoStoreZone extends Zone {
    public NoStoreZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_STORE, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_STORE, false);
        }
    }
}
