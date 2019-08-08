package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneArea;

/**
 * Just dummy zone, needs only for geometry calculations
 *
 * @author UnAfraid
 */
public class BannedSpawnTerritory {
    private final String name;
    private final ZoneArea area;

    public BannedSpawnTerritory(String name, ZoneArea area) {
        this.name = name;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public boolean isInsideZone(int x, int y, int z) {
        return area.isInsideZone(x, y, z);
    }
}