package org.l2j.gameserver.model.spawns;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.model.interfaces.ITerritorized;
import org.l2j.gameserver.model.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.model.zone.type.SpawnTerritory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid
 */
public class SpawnGroup implements Cloneable, ITerritorized, IParameterized<StatsSet> {
    private final String _name;
    private final boolean _spawnByDefault;
    private final List<NpcSpawnTemplate> spawns = new ArrayList<>();
    private List<SpawnTerritory> _territories;
    private List<BannedSpawnTerritory> _bannedTerritories;
    private StatsSet _parameters;

    public SpawnGroup(StatsSet set) {
        this(set.getString("name", null), set.getBoolean("spawnByDefault", true));
    }

    private SpawnGroup(String name, boolean spawnByDefault) {
        _name = name;
        _spawnByDefault = spawnByDefault;
    }

    public String getName() {
        return _name;
    }

    public boolean isSpawningByDefault() {
        return _spawnByDefault;
    }

    public void addSpawn(NpcSpawnTemplate template) {
        spawns.add(template);
    }

    public List<NpcSpawnTemplate> getSpawns() {
        return spawns;
    }

    @Override
    public void addTerritory(SpawnTerritory territory) {
        if (_territories == null) {
            _territories = new ArrayList<>();
        }
        _territories.add(territory);
    }

    @Override
    public List<SpawnTerritory> getTerritories() {
        return _territories != null ? _territories : Collections.emptyList();
    }

    @Override
    public void addBannedTerritory(BannedSpawnTerritory territory) {
        if (_bannedTerritories == null) {
            _bannedTerritories = new ArrayList<>();
        }
        _bannedTerritories.add(territory);
    }

    @Override
    public List<BannedSpawnTerritory> getBannedTerritories() {
        return _bannedTerritories != null ? _bannedTerritories : Collections.emptyList();
    }

    @Override
    public StatsSet getParameters() {
        return _parameters;
    }

    @Override
    public void setParameters(StatsSet parameters) {
        _parameters = parameters;
    }

    public void spawnAll() {
        spawnAll(null);
    }

    public void spawnAll(Instance instance) {
        spawns.parallelStream().forEach(template -> template.spawn(instance));
    }

    public void despawnAll() {
        spawns.forEach(NpcSpawnTemplate::despawn);
    }

    @Override
    public SpawnGroup clone() {
        final SpawnGroup group = new SpawnGroup(_name, _spawnByDefault);

        // Clone banned territories
        for (BannedSpawnTerritory territory : getBannedTerritories()) {
            group.addBannedTerritory(territory);
        }

        // Clone territories
        for (SpawnTerritory territory : getTerritories()) {
            group.addTerritory(territory);
        }

        // Clone spawns
        for (NpcSpawnTemplate spawn : spawns) {
            group.addSpawn(spawn.clone());
        }

        return group;
    }
}
