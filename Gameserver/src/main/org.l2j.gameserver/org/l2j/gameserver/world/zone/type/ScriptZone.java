package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * A scripted zone... Creation of such a zone should require somekind of script reference which can handle onEnter() / onExit()
 *
 * @author durgus
 */
public class ScriptZone extends Zone {
    public ScriptZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.SCRIPT, true);
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.SCRIPT, false);
    }
}
