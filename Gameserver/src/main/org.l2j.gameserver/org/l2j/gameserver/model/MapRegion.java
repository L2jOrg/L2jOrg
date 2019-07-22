package org.l2j.gameserver.model;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.Race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nyaran
 */
public class MapRegion {
    private final String _name;
    private final String _town;
    private final int _locId;
    private final int _bbs;
    private final Map<Race, String> _bannedRace = new HashMap<>();
    private List<int[]> _maps = null;
    private List<Location> _spawnLocs = null;
    private List<Location> _chaoticSpawnLocs = null;

    public MapRegion(String name, String town, int locId, int bbs) {
        _name = name;
        _town = town;
        _locId = locId;
        _bbs = bbs;
    }

    public final String getName() {
        return _name;
    }

    public final String getTown() {
        return _town;
    }

    public final int getLocId() {
        return _locId;
    }

    public final int getBbs() {
        return _bbs;
    }

    public final void addMap(int x, int y) {
        if (_maps == null) {
            _maps = new ArrayList<>();
        }

        _maps.add(new int[]{ x, y});
    }

    public final List<int[]> getMaps() {
        return _maps;
    }

    public final boolean isZoneInRegion(int x, int y) {
        if (_maps == null) {
            return false;
        }

        for (int[] map : _maps) {
            if ((map[0] == x) && (map[1] == y)) {
                return true;
            }
        }
        return false;
    }

    // Respawn
    public final void addSpawn(int x, int y, int z) {
        if (_spawnLocs == null) {
            _spawnLocs = new ArrayList<>();
        }

        _spawnLocs.add(new Location(x, y, z));
    }

    public final void addChaoticSpawn(int x, int y, int z) {
        if (_chaoticSpawnLocs == null) {
            _chaoticSpawnLocs = new ArrayList<>();
        }

        _chaoticSpawnLocs.add(new Location(x, y, z));
    }

    public final List<Location> getSpawns() {
        return _spawnLocs;
    }

    public final Location getSpawnLoc() {
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return _spawnLocs.get(Rnd.get(_spawnLocs.size()));
        }
        return _spawnLocs.get(0);
    }

    public final Location getChaoticSpawnLoc() {
        if (_chaoticSpawnLocs != null) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return _chaoticSpawnLocs.get(Rnd.get(_chaoticSpawnLocs.size()));
            }
            return _chaoticSpawnLocs.get(0);
        }
        return getSpawnLoc();
    }


    public final void addBannedRace(String race, String point) {
        _bannedRace.put(Race.valueOf(race), point);
    }

    public final Map<Race, String> getBannedRace() {
        return _bannedRace;
    }
}
