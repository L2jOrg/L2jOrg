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
package org.l2j.gameserver.model.spawns;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.ChanceLocation;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.util.GameUtils.isMonster;


/**
 * @author UnAfraid
 */
public class NpcSpawnTemplate implements Cloneable, IParameterized<StatsSet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpawnTemplate.class);

    private final int id;
    private final int _count;
    private final Duration respawnTime;
    private final Duration respawnTimeRandom;
    private final boolean _spawnAnimation;
    private final boolean saveInDB;
    private final String _dbName;
    private final SpawnTemplate spawnTemplate;
    private final SpawnGroup group;
    private final Set<Npc> _spawnedNpcs = ConcurrentHashMap.newKeySet();
    private List<ChanceLocation> locations;
    private SpawnTerritory zone;
    private StatsSet _parameters;
    private List<MinionHolder> _minions;

    private NpcSpawnTemplate(NpcSpawnTemplate template) {
        spawnTemplate = template.spawnTemplate;
        group = template.group;
        id = template.id;
        _count = template._count;
        respawnTime = template.respawnTime;
        respawnTimeRandom = template.respawnTimeRandom;
        _spawnAnimation = template._spawnAnimation;
        saveInDB = template.saveInDB;
        _dbName = template._dbName;
        locations = template.locations;
        zone = template.zone;
        _parameters = template._parameters;
        _minions = template._minions;
    }

    public NpcSpawnTemplate(SpawnTemplate spawnTemplate, SpawnGroup group, StatsSet set) {
        this.spawnTemplate = spawnTemplate;
        this.group = group;
        id = set.getInt("id");
        _count = set.getInt("count", 1);
        respawnTime = set.getDuration("respawnTime", null);
        respawnTimeRandom = set.getDuration("respawnRandom", null);
        _spawnAnimation = set.getBoolean("spawnAnimation", false);
        saveInDB = set.getBoolean("dbSave", false);
        _dbName = set.getString("dbName", null);
        _parameters = mergeParameters(spawnTemplate, group);

        final int x = set.getInt("x", Integer.MAX_VALUE);
        final int y = set.getInt("y", Integer.MAX_VALUE);
        final int z = set.getInt("z", Integer.MAX_VALUE);
        final boolean xDefined = x != Integer.MAX_VALUE;
        final boolean yDefined = y != Integer.MAX_VALUE;
        final boolean zDefined = z != Integer.MAX_VALUE;
        if (xDefined && yDefined && zDefined) {
            locations = new ArrayList<>();
            locations.add(new ChanceLocation(x, y, z, set.getInt("heading", 0), 100));
        } else {
            if (xDefined || yDefined || zDefined) {
                throw new IllegalStateException(String.format("Spawn with partially declared and x: %s y: %s z: %s!", processParam(x), processParam(y), processParam(z)));
            }

            final String zoneName = set.getString("zone", null);
            if (zoneName != null) {
                final SpawnTerritory zone = ZoneManager.getInstance().getSpawnTerritory(zoneName);
                if (zone == null) {
                    throw new NullPointerException("Spawn with non existing zone requested " + zoneName);
                }
                this.zone = zone;
            }
        }

        mergeParameters(spawnTemplate, group);
    }

    private StatsSet mergeParameters(SpawnTemplate spawnTemplate, SpawnGroup group) {
        if ((_parameters == null) && (spawnTemplate.getParameters() == null) && (group.getParameters() == null)) {
            return null;
        }

        final StatsSet set = new StatsSet();
        if (spawnTemplate.getParameters() != null) {
            set.merge(spawnTemplate.getParameters());
        }
        if (group.getParameters() != null) {
            set.merge(group.getParameters());
        }
        if (_parameters != null) {
            set.merge(_parameters);
        }
        return set;
    }

    public void addSpawnLocation(ChanceLocation loc) {
        if (locations == null) {
            locations = new ArrayList<>();
        }
        locations.add(loc);
    }

    public SpawnTemplate getSpawnTemplate() {
        return spawnTemplate;
    }

    public SpawnGroup getGroup() {
        return group;
    }

    private String processParam(int value) {
        return value != Integer.MAX_VALUE ? Integer.toString(value) : "undefined";
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return _count;
    }

    public Duration getRespawnTime() {
        return respawnTime;
    }

    public Duration getRespawnTimeRandom() {
        return respawnTimeRandom;
    }

    public List<ChanceLocation> getLocation() {
        return locations;
    }

    public SpawnTerritory getZone() {
        return zone;
    }

    @Override
    public StatsSet getParameters() {
        return _parameters;
    }

    @Override
    public void setParameters(StatsSet parameters) {
        if (_parameters == null) {
            _parameters = parameters;
        } else {
            _parameters.merge(parameters);
        }
    }

    public boolean hasSpawnAnimation() {
        return _spawnAnimation;
    }

    public boolean hasDBSave() {
        return saveInDB;
    }

    public String getDBName() {
        return _dbName;
    }

    public List<MinionHolder> getMinions() {
        return _minions != null ? _minions : Collections.emptyList();
    }

    public void addMinion(MinionHolder minion) {
        if (_minions == null) {
            _minions = new ArrayList<>();
        }
        _minions.add(minion);
    }

    public Set<Npc> getSpawnedNpcs() {
        return _spawnedNpcs;
    }

    public final Location getSpawnLocation() {
        if (!isNullOrEmpty(locations)) {
            final double locRandom = Rnd.get(100);
            float cumulativeChance = 0;
            for (ChanceLocation loc : locations) {
                if (locRandom <= (cumulativeChance += loc.getChance())) {
                    return loc;
                }
            }
            LOGGER.warn("Couldn't match location by chance returning first..");
            return locations.get(0);
        } else if (nonNull(zone)) {
            final Location loc = zone.getRandomPoint();
            loc.setHeading(Rnd.get(65535));
            return loc;
        } else if (!group.getTerritories().isEmpty()) {
            return getRandomLocation(group.getTerritories(), group.getBannedTerritories());
        } else if (!spawnTemplate.getTerritories().isEmpty()) {
            return getRandomLocation(spawnTemplate.getTerritories(), spawnTemplate.getBannedTerritories());
        }
        return null;
    }

    private Location getRandomLocation(List<SpawnTerritory> territories, List<BannedSpawnTerritory> bannedTerritories) {
        final SpawnTerritory territory = Rnd.get(territories);
        if(nonNull(territory)) {
            for (int i = 0; i < 25; i++) {
                Location loc = territory.getRandomPoint();
                if (bannedTerritories.isEmpty() || bannedTerritories.stream().noneMatch(bannedTerritory -> bannedTerritory.isInsideZone(loc.getX(), loc.getY(), loc.getZ()))) {
                    return loc;
                }
            }
        }
        return null;
    }

    public void spawn() {
        spawn(null);
    }

    public void spawn(Instance instance) {
        try {
            final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(id);

            if (isNull(npcTemplate)) {
                LOGGER.warn("Attempting to spawn unexisting npc id: {} file: {} spawn {} group {}", id,  spawnTemplate.getFilePath(), spawnTemplate.getName(), group.getName());
                return;
            }

            if (npcTemplate.isType("Defender")) {
                LOGGER.warn("Attempting to spawn npc id: {} type: {} file: {} spawn: {} group: {}", id, npcTemplate.getType(),  spawnTemplate.getFilePath(), spawnTemplate.getName(), group.getName());
                return;
            }


            for (int i = 0; i < _count; i++) {
                spawnNpc(npcTemplate, instance);
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't spawn npc " + id, e);
        }
    }

    private void spawnNpc(NpcTemplate npcTemplate, Instance instance) throws SecurityException, ClassNotFoundException, NoSuchMethodException, ClassCastException {
        final Spawn spawn = new Spawn(npcTemplate);
        final Location loc = getSpawnLocation();
        if (isNull(loc)) {
            LOGGER.warn("Couldn't initialize new spawn, no location found!");
            return;
        }

        spawn.setInstanceId(zeroIfNullOrElse(instance, Instance::getId));
        spawn.setAmount(1);
        spawn.setLocation(loc);
        int respawn = zeroIfNullOrElse(respawnTime, r -> (int) r.getSeconds());
        int respawnRandom = zeroIfNullOrElse(respawnTimeRandom, r -> (int) r.getSeconds());

        if (respawn > 0) {
            spawn.setRespawnDelay(respawn, respawnRandom);
            spawn.startRespawn();
        } else {
            spawn.stopRespawn();
        }

        spawn.setSpawnTemplate(this);

        if (saveInDB) {
            if (!DBSpawnManager.getInstance().isDefined(id)) {
                final Npc spawnedNpc = DBSpawnManager.getInstance().addNewSpawn(spawn, true);
                if (isMonster(spawnedNpc) && (_minions != null)) {
                    ((Monster) spawnedNpc).getMinionList().spawnMinions(_minions);
                }

                _spawnedNpcs.add(spawnedNpc);
            }
        } else {
            final Npc npc = spawn.doSpawn(_spawnAnimation);
            if (isMonster(npc) && (_minions != null)) {
                ((Monster) npc).getMinionList().spawnMinions(_minions);
            }
            _spawnedNpcs.add(npc);

            SpawnTable.getInstance().addNewSpawn(spawn, false);
        }
    }

    public void despawn() {
        _spawnedNpcs.forEach(npc ->
        {
            npc.getSpawn().stopRespawn();
            SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
            npc.deleteMe();
        });
        _spawnedNpcs.clear();
    }

    public void notifySpawnNpc(Npc npc) {
        spawnTemplate.notifyEvent(event -> event.onSpawnNpc(spawnTemplate, group, npc));
    }

    public void notifyDespawnNpc(Npc npc) {
        spawnTemplate.notifyEvent(event -> event.onSpawnDespawnNpc(spawnTemplate, group, npc));
    }

    public void notifyNpcDeath(Npc npc, Creature killer) {
        spawnTemplate.notifyEvent(event -> event.onSpawnNpcDeath(spawnTemplate, group, npc, killer));
    }

    @Override
    public NpcSpawnTemplate clone() {
        return new NpcSpawnTemplate(this);
    }
}