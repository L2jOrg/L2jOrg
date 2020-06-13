/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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