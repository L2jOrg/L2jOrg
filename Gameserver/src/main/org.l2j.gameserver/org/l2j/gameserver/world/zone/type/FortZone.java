package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * A castle zone
 *
 * @author durgus
 */
public final class FortZone extends ResidenceZone {
    public FortZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("fortId")) {
            setResidenceId(Integer.parseInt(value));
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.FORT, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.FORT, false);
    }
}