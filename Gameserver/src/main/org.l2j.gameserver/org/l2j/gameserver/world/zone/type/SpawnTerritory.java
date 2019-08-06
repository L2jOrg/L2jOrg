package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.ZoneArea;

/**
 * Just dummy zone, needs only for geometry calculations
 *
 * @author GKR
 */
public class SpawnTerritory {
    private final String _name;
    private final ZoneArea _territory;

    public SpawnTerritory(String name, ZoneArea territory) {
        _name = name;
        _territory = territory;
    }

    public String getName() {
        return _name;
    }

    public Location getRandomPoint() {
        return _territory.getRandomPoint();
    }

    public boolean isInsideZone(int x, int y, int z) {
        return _territory.isInsideZone(x, y, z);
    }

    public void visualizeZone(int z) {
        _territory.visualizeZone(z);
    }
}