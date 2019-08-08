package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * The Monster Derby Track Zone
 *
 * @author durgus
 */
public class DerbyTrackZone extends Zone {
    public DerbyTrackZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayable(creature)) {
            creature.setInsideZone(ZoneType.MONSTER_TRACK, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayable(creature)) {
            creature.setInsideZone(ZoneType.MONSTER_TRACK, false);
        }
    }
}
