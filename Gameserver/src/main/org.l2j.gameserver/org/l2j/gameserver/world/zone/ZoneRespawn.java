package org.l2j.gameserver.world.zone;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract zone with spawn locations
 *
 * @author DS, Nyaran (rework 10/07/2011)
 */
public abstract class ZoneRespawn extends Zone {
    private List<Location> _spawnLocs = null;
    private List<Location> _otherSpawnLocs = null;
    private List<Location> _chaoticSpawnLocs = null;
    private List<Location> _banishSpawnLocs = null;

    protected ZoneRespawn(int id) {
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
        if (_spawnLocs == null) {
            _spawnLocs = new ArrayList<>();
        }

        _spawnLocs.add(new Location(x, y, z));
    }

    private void addOtherSpawn(int x, int y, int z) {
        if (_otherSpawnLocs == null) {
            _otherSpawnLocs = new ArrayList<>();
        }

        _otherSpawnLocs.add(new Location(x, y, z));
    }

    private void addChaoticSpawn(int x, int y, int z) {
        if (_chaoticSpawnLocs == null) {
            _chaoticSpawnLocs = new ArrayList<>();
        }

        _chaoticSpawnLocs.add(new Location(x, y, z));
    }

    private void addBanishSpawn(int x, int y, int z) {
        if (_banishSpawnLocs == null) {
            _banishSpawnLocs = new ArrayList<>();
        }

        _banishSpawnLocs.add(new Location(x, y, z));
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

    public final Location getOtherSpawnLoc() {
        if (_otherSpawnLocs != null) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return _otherSpawnLocs.get(Rnd.get(_otherSpawnLocs.size()));
            }
            return _otherSpawnLocs.get(0);
        }
        return getSpawnLoc();
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

    public Location getBanishSpawnLoc() {
        if (_banishSpawnLocs != null) {
            if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
                return _banishSpawnLocs.get(Rnd.get(_banishSpawnLocs.size()));
            }
            return _banishSpawnLocs.get(0);
        }
        return getSpawnLoc();
    }
}
