package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Zone where 'Build Headquarters' is allowed.
 *
 * @author Gnacik
 */
public class HqZone extends Zone {
    public HqZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if ("castleId".equals(name)) {
            // TODO
        } else if ("fortId".equals(name)) {
            // TODO
        } else if ("clanHallId".equals(name)) {
            // TODO
        } else if ("territoryId".equals(name)) {
            // TODO
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.HQ, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.HQ, false);
        }
    }
}
