package org.l2j.gameserver.model.spawns;

import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.model.interfaces.ITerritorized;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class SpawnTemplate implements Cloneable, ITerritorized, IParameterized<StatsSet> {
    private final String _name;
    private final String _ai;
    private final boolean _spawnByDefault;
    private final String filePath;
    private final List<SpawnGroup> groups = new LinkedList<>();
    private List<SpawnTerritory> _territories;
    private List<BannedSpawnTerritory> _bannedTerritories;
    private StatsSet _parameters;

    public SpawnTemplate(StatsSet set, String file) {
        this(set.getString("name", null), set.getString("ai", null), set.getBoolean("spawnByDefault", true), file);
    }

    private SpawnTemplate(String name, String ai, boolean spawnByDefault, String file) {
        _name = name;
        _ai = ai;
        _spawnByDefault = spawnByDefault;
        filePath = file;
    }

    public String getName() {
        return _name;
    }

    public String getAI() {
        return _ai;
    }

    public boolean isSpawningByDefault() {
        return _spawnByDefault;
    }

    public String getFilePath() {
        return filePath;
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

    public void addGroup(SpawnGroup group) {
        groups.add(group);
    }

    public List<SpawnGroup> getGroups() {
        return groups;
    }

    public List<SpawnGroup> getGroupsByName(String name) {
        return groups.stream().filter(group -> String.valueOf(group.getName()).equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    @Override
    public StatsSet getParameters() {
        return _parameters;
    }

    @Override
    public void setParameters(StatsSet parameters) {
        _parameters = parameters;
    }

    public void notifyEvent(Consumer<Quest> event) {
        if (_ai != null) {
            final Quest script = QuestManager.getInstance().getQuest(_ai);
            if (script != null) {
                event.accept(script);
            }
        }
    }

    public void spawn(Predicate<SpawnGroup> groupFilter, Instance instance) {
        groups.parallelStream().filter(groupFilter).forEach(group -> group.spawnAll(instance));
    }

    public void spawnAll(Instance instance) {
        spawn(SpawnGroup::isSpawningByDefault, instance);
    }

    public void notifyActivate() {
        notifyEvent(script -> script.onSpawnActivate(this));
    }

    public void spawnAllIncludingNotDefault(Instance instance) {
        groups.forEach(group -> group.spawnAll(instance));
    }

    public void despawn(Predicate<SpawnGroup> groupFilter) {
        groups.stream().filter(groupFilter).forEach(SpawnGroup::despawnAll);
        notifyEvent(script -> script.onSpawnDeactivate(this));
    }

    public void despawnAll() {
        groups.forEach(SpawnGroup::despawnAll);
        notifyEvent(script -> script.onSpawnDeactivate(this));
    }

    @Override
    public SpawnTemplate clone() {
        final SpawnTemplate template = new SpawnTemplate(_name, _ai, _spawnByDefault, filePath);

        // Clone parameters
        template.setParameters(_parameters);

        // Clone banned territories
        for (BannedSpawnTerritory territory : getBannedTerritories()) {
            template.addBannedTerritory(territory);
        }

        // Clone territories
        for (SpawnTerritory territory : getTerritories()) {
            template.addTerritory(territory);
        }

        // Clone groups
        for (SpawnGroup group : groups) {
            template.addGroup(group.clone());
        }

        return template;
    }
}
