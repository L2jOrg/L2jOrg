package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

/**
 * @author Sdw
 */
public class TeleportZone extends Zone {
    private int x = -1;
    private int y = -1;
    private int z = -1;

    public TeleportZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "oustX" -> x = Integer.parseInt(value);
            case "oustY" -> y = Integer.parseInt(value);
            case "oustZ" -> z = Integer.parseInt(value);
            default -> super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.teleToLocation(new Location(x, y, z));
    }

    @Override
    protected void onExit(Creature creature) {
    }
}