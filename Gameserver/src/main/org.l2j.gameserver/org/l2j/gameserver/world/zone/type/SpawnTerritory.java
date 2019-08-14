package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.ZoneArea;

/**
 * Just dummy zone, needs only for geometry calculations
 *
 * @author GKR
 */
public class SpawnTerritory {
    private final String name;
    private final ZoneArea territory;

    public SpawnTerritory(String name, ZoneArea territory) {
        this.name = name;
        this.territory = territory;
    }

    public String getName() {
        return name;
    }

    public Location getRandomPoint() {
        return territory.getRandomPoint();
    }

    public boolean isInsideZone(int x, int y, int z) {
        return territory.isInsideZone(x, y, z);
    }

    public void visualizeZone(int z) {
        territory.visualizeZone(z);
    }
}