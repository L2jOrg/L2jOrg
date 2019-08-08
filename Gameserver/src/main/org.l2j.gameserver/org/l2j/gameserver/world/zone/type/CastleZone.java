package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * A castle zone
 *
 * @author durgus
 */
public final class CastleZone extends ResidenceZone {

    public CastleZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("castleId")) {
            setResidenceId(Integer.parseInt(value));
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.CASTLE, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.CASTLE, false);
    }
}
