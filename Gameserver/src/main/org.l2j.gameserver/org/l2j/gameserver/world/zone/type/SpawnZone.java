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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract zone with spawn locations
 *
 * @author DS, Nyaran (rework 10/07/2011)
 */
public abstract class SpawnZone extends Zone {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpawnZone.class);

    private List<Location> spawnLocs = null;
    private List<Location> otherSpawnLocs = null;
    private List<Location> chaoticSpawnLocs = null;
    private List<Location> banishSpawnLocs = null;

    SpawnZone(int id) {
        super(id);
    }

    public void parseLoc(int x, int y, int z, String type) {
        if ((type == null) || type.isEmpty()) {
            addSpawn(x, y, z);
        } else {
            switch (type) {
                case "other" -> addOtherSpawn(x, y, z);
                case "chaotic" -> addChaoticSpawn(x, y, z);
                case "banish" -> addBanishSpawn(x, y, z);
                default -> LOGGER.warn("Unknown location type: {}", type);
            }
        }
    }

    public final void addSpawn(int x, int y, int z) {
        if (spawnLocs == null) {
            spawnLocs = new ArrayList<>();
        }

        spawnLocs.add(new Location(x, y, z));
    }

    private void addOtherSpawn(int x, int y, int z) {
        if (otherSpawnLocs == null) {
            otherSpawnLocs = new ArrayList<>();
        }

        otherSpawnLocs.add(new Location(x, y, z));
    }

    private void addChaoticSpawn(int x, int y, int z) {
        if (chaoticSpawnLocs == null) {
            chaoticSpawnLocs = new ArrayList<>();
        }

        chaoticSpawnLocs.add(new Location(x, y, z));
    }

    private void addBanishSpawn(int x, int y, int z) {
        if (banishSpawnLocs == null) {
            banishSpawnLocs = new ArrayList<>();
        }

        banishSpawnLocs.add(new Location(x, y, z));
    }

    public final List<Location> getSpawns() {
        return spawnLocs;
    }

    public final Location getSpawnLoc() {
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return spawnLocs.get(Rnd.get(spawnLocs.size()));
        }
        return spawnLocs.get(0);
    }

    public final Location getOtherSpawnLoc() {
        if (otherSpawnLocs != null) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return otherSpawnLocs.get(Rnd.get(otherSpawnLocs.size()));
            }
            return otherSpawnLocs.get(0);
        }
        return getSpawnLoc();
    }

    public final Location getChaoticSpawnLoc() {
        if (chaoticSpawnLocs != null) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return chaoticSpawnLocs.get(Rnd.get(chaoticSpawnLocs.size()));
            }
            return chaoticSpawnLocs.get(0);
        }
        return getSpawnLoc();
    }

    public Location getBanishSpawnLoc() {
        if (banishSpawnLocs != null) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return banishSpawnLocs.get(Rnd.get(banishSpawnLocs.size()));
            }
            return banishSpawnLocs.get(0);
        }
        return getSpawnLoc();
    }
}
