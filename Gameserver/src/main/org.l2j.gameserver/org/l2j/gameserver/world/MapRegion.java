package org.l2j.gameserver.world;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Nyaran
 */
public class MapRegion {
    private final String town;
    private final int locId;
    private final int bbs;
    private final Map<Race, String> bannedRaces = new HashMap<>();
    private List<Tile> tiles = null;
    private List<Location> spawnLocs = null;
    private List<Location> chaoticSpawnLocs = null;

    public MapRegion(String town, int locId, int bbs) {
        this.town = town;
        this.locId = locId;
        this.bbs = bbs;
    }

    final void addMapTile(byte x, byte y) {
        if (tiles == null) {
            tiles = new ArrayList<>();
        }

        tiles.add(new Tile(x, y));
    }

    final boolean isZoneInRegion(int x, int y) {
        if (isNull(tiles)) {
            return false;
        }
        return tiles.stream().anyMatch(tile -> tile.isSame(x, y));
    }

    // Respawn
    public final void addSpawn(int x, int y, int z) {
        if (isNull(spawnLocs)) {
            spawnLocs = new ArrayList<>();
        }

        spawnLocs.add(new Location(x, y, z));
    }

    final void addChaoticSpawn(int x, int y, int z) {
        if (isNull(chaoticSpawnLocs)) {
            chaoticSpawnLocs = new ArrayList<>();
        }

        chaoticSpawnLocs.add(new Location(x, y, z));
    }

    public final Location getSpawnLoc() {
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return Rnd.get(spawnLocs);
        }
        return spawnLocs.get(0);
    }

    final Location getChaoticSpawnLoc() {
        if (nonNull(chaoticSpawnLocs)) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return Rnd.get(chaoticSpawnLocs);
            }
            return chaoticSpawnLocs.get(0);
        }
        return getSpawnLoc();
    }

    final void addBannedRace(String race, String point) {
        bannedRaces.put(Race.valueOf(race), point);
    }

    final Map<Race, String> getBannedRaces() {
        return bannedRaces;
    }

    public final String getTown() {
        return town;
    }

    public final int getLocId() {
        return locId;
    }

    public final int getBbs() {
        return bbs;
    }

    private static class Tile {
        private byte x;
        private byte y;

        private Tile(byte x, byte y) {
            this.x = x;
            this.y = y;
        }

        private boolean isSame(int x, int y) {
            return this.x == x && this.y == y;
        }
    }

}
