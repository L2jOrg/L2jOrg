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
package org.l2j.gameserver.model.instancezone;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.enums.InstanceTeleportType;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.instance.*;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Instance world.
 *
 * @author malyelfik
 */
public final class Instance implements IIdentifiable, INamable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Instance.class);

    // Basic instance parameters
    private final int _id;
    private final InstanceTemplate _template;

    // Advanced instance parameters
    private final Set<Player> _allowed = ConcurrentHashMap.newKeySet(); // Players which can enter to instance
    private final Set<Player> _players = ConcurrentHashMap.newKeySet(); // Players inside instance
    private final Set<Npc> _npcs = ConcurrentHashMap.newKeySet(); // Spawned NPCs inside instance
    private final Map<Integer, Door> _doors = new HashMap<>(); // Spawned doors inside instance
    private final StatsSet _parameters = new StatsSet();
    // Timers
    private final Map<Integer, ScheduledFuture<?>> _ejectDeadTasks = new ConcurrentHashMap<>();
    private final List<SpawnTemplate> _spawns;
    private long _endTime;
    private ScheduledFuture<?> _cleanUpTask = null;
    private ScheduledFuture<?> _emptyDestroyTask = null;

    /**
     * Create instance world.
     *
     * @param id       ID of instance world
     * @param template template of instance world
     * @param player   player who create instance world.
     */
    public Instance(int id, InstanceTemplate template, Player player) {
        // Set basic instance info
        _id = id;
        _template = template;
        _spawns = new ArrayList<>(template.getSpawns().size());

        // Clone and add the spawn templates
        template.getSpawns().stream().map(SpawnTemplate::clone).forEach(_spawns::add);

        // Register world to instance manager.
        InstanceManager.getInstance().register(this);

        // Set duration, spawns, status, etc..
        setDuration(_template.getDuration());
        setStatus(0);
        spawnDoors();

        // initialize instance spawns
        _spawns.stream().filter(SpawnTemplate::isSpawningByDefault).forEach(spawnTemplate -> spawnTemplate.spawnAll(this));

        if (!isDynamic()) {
            // Notify DP scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnInstanceCreated(this, player), _template);
        }
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public String getName() {
        return _template.getName();
    }

    /**
     * Check if instance has been created dynamically or have XML template.
     *
     * @return {@code true} if instance is dynamic or {@code false} if instance has static template
     */
    public boolean isDynamic() {
        return _template.getId() == -1;
    }

    /**
     * Set instance world parameter.
     *
     * @param key parameter name
     * @param val parameter value
     */
    public void setParameter(String key, Object val) {
        if (val == null) {
            _parameters.remove(key);
        } else {
            _parameters.set(key, val);
        }
    }

    /**
     * Get instance world parameters.
     *
     * @return instance parameters
     */
    public StatsSet getParameters() {
        return _parameters;
    }

    /**
     * Get status of instance world.
     *
     * @return instance status, otherwise 0
     */
    public int getStatus() {
        return _parameters.getInt("INSTANCE_STATUS", 0);
    }

    /**
     * Set status of instance world.
     *
     * @param value new world status
     */
    public void setStatus(int value) {
        _parameters.set("INSTANCE_STATUS", value);
        EventDispatcher.getInstance().notifyEventAsync(new OnInstanceStatusChange(this, value), _template);
    }

    /**
     * Increment instance world status
     *
     * @return new world status
     */
    public int incStatus() {
        final int status = getStatus() + 1;
        setStatus(status);
        return status;
    }

    /**
     * Add player who can enter to instance.
     *
     * @param player player instance
     */
    public void addAllowed(Player player) {
        if (!_allowed.contains(player)) {
            _allowed.add(player);
        }
    }

    /**
     * Check if player can enter to instance.
     *
     * @param player player itself
     * @return {@code true} when can enter, otherwise {@code false}
     */
    public boolean isAllowed(Player player) {
        return _allowed.contains(player);
    }

    /**
     * Returns all players who can enter to instance.
     *
     * @return allowed players list
     */
    public Set<Player> getAllowed() {
        return _allowed;
    }

    /**
     * Add player to instance
     *
     * @param player player instance
     */
    public void addPlayer(Player player) {
        _players.add(player);
        if (_emptyDestroyTask != null) {
            _emptyDestroyTask.cancel(false);
            _emptyDestroyTask = null;
        }
    }

    /**
     * Remove player from instance.
     *
     * @param player player instance
     */
    public void removePlayer(Player player) {
        _players.remove(player);
        if (_players.isEmpty()) {
            final long emptyTime = _template.getEmptyDestroyTime();
            if ((_template.getDuration() == 0) || (emptyTime == 0)) {
                destroy();
            } else if ((emptyTime >= 0) && (_emptyDestroyTask == null) && (getRemainingTime() < emptyTime)) {
                _emptyDestroyTask = ThreadPool.schedule(this::destroy, emptyTime);
            }
        }
    }

    /**
     * Check if player is inside instance.
     *
     * @param player player to be checked
     * @return {@code true} if player is inside, otherwise {@code false}
     */
    public boolean containsPlayer(Player player) {
        return _players.contains(player);
    }

    /**
     * Get all players inside instance.
     *
     * @return players within instance
     */
    public Set<Player> getPlayers() {
        return _players;
    }

    /**
     * Get count of players inside instance.
     *
     * @return players count inside instance
     */
    public int getPlayersCount() {
        return _players.size();
    }

    /**
     * Spawn doors inside instance world.
     */
    private void spawnDoors() {
        for (DoorTemplate template : _template.getDoors().values()) {
            // Create new door instance
            _doors.put(template.getId(), DoorDataManager.getInstance().spawnDoor(template, this));
        }
    }

    /**
     * Get all doors spawned inside instance world.
     *
     * @return collection of spawned doors
     */
    public Collection<Door> getDoors() {
        return _doors.values();
    }

    /**
     * Get spawned door by template ID.
     *
     * @param id template ID of door
     * @return instance of door if found, otherwise {@code null}
     */
    public Door getDoor(int id) {
        return _doors.get(id);
    }

    /**
     * Get spawn group by group name.
     *
     * @param name name of group
     * @return list which contains spawn data from spawn group
     */
    public List<SpawnGroup> getSpawnGroup(String name) {
        final List<SpawnGroup> spawns = new ArrayList<>();
        _spawns.forEach(spawnTemplate -> spawns.addAll(spawnTemplate.getGroupsByName(name)));
        return spawns;
    }

    /**
     * Spawn NPCs from group (defined in XML template) into instance world.
     *
     * @param name name of group which should be spawned
     * @return list that contains NPCs spawned by this method
     */
    public List<Npc> spawnGroup(String name) {
        final List<SpawnGroup> spawns = getSpawnGroup(name);
        if (spawns == null) {
            LOGGER.warn("Spawn group " + name + " doesn't exist for instance " + _template.getName() + " (" + _id + ")!");
            return Collections.emptyList();
        }

        final List<Npc> npcs = new LinkedList<>();
        try {
            for (SpawnGroup holder : spawns) {
                holder.spawnAll(this);
                holder.getSpawns().forEach(spawn -> npcs.addAll(spawn.getSpawnedNpcs()));
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to spawn group " + name + " inside instance " + _template.getName() + " (" + _id + ")");
        }
        return npcs;
    }


    /**
     * Get spawned NPCs from instance.
     *
     * @return set of NPCs from instance
     */
    public Set<Npc> getNpcs() {
        return _npcs;
    }

    /**
     * Get alive NPCs from instance.
     *
     * @return set of NPCs from instance
     */
    public Set<Npc> getAliveNpcs() {
        return _npcs.stream().filter(n -> n.getCurrentHp() > 0).collect(Collectors.toSet());
    }

    /**
     * Get first found spawned NPC with specific ID.
     *
     * @param id ID of NPC to be found
     * @return first found NPC with specified ID, otherwise {@code null}
     */
    public Npc getNpc(int id) {
        return _npcs.stream().filter(n -> n.getId() == id).findFirst().orElse(null);
    }

    public void addNpc(Npc npc) {
        _npcs.add(npc);
    }

    public void removeNpc(Npc npc) {
        _npcs.remove(npc);
    }

    /**
     * Remove all players from instance world.
     */
    private void removePlayers() {
        _players.forEach(this::ejectPlayer);
        _players.clear();
    }

    /**
     * Despawn doors inside instance world.
     */
    private void removeDoors() {
        _doors.values().stream().filter(Objects::nonNull).forEach(Door::decayMe);
        _doors.clear();
    }

    /**
     * Despawn NPCs inside instance world.
     */
    public void removeNpcs() {
        _spawns.forEach(SpawnTemplate::despawnAll);
        _npcs.forEach(Npc::deleteMe);
        _npcs.clear();
    }

    /**
     * Change instance duration.
     *
     * @param minutes remaining time to destroy instance
     */
    public void setDuration(int minutes) {
        // Instance never ends
        if (minutes < 0) {
            _endTime = -1;
            return;
        }

        // Stop running tasks
        final long millis = TimeUnit.MINUTES.toMillis(minutes);
        if (_cleanUpTask != null) {
            _cleanUpTask.cancel(true);
            _cleanUpTask = null;
        }

        if ((_emptyDestroyTask != null) && (millis < _emptyDestroyTask.getDelay(TimeUnit.MILLISECONDS))) {
            _emptyDestroyTask.cancel(true);
            _emptyDestroyTask = null;
        }

        // Set new cleanup task
        _endTime = System.currentTimeMillis() + millis;
        if (minutes < 1) // Destroy instance
        {
            destroy();
        } else {
            sendWorldDestroyMessage(minutes);
            if (minutes <= 5) // Message 1 minute before destroy
            {
                _cleanUpTask = ThreadPool.schedule(this::cleanUp, millis - 60000);
            } else // Message 5 minutes before destroy
            {
                _cleanUpTask = ThreadPool.schedule(this::cleanUp, millis - (5 * 60000));
            }
        }
    }

    /**
     * Destroy current instance world.<br>
     * <b><font color=red>Use this method to destroy instance world properly.</font></b>
     */
    public synchronized void destroy() {
        if (_cleanUpTask != null) {
            _cleanUpTask.cancel(false);
            _cleanUpTask = null;
        }

        if (_emptyDestroyTask != null) {
            _emptyDestroyTask.cancel(false);
            _emptyDestroyTask = null;
        }

        _ejectDeadTasks.values().forEach(t -> t.cancel(true));
        _ejectDeadTasks.clear();

        // Notify DP scripts
        if (!isDynamic()) {
            EventDispatcher.getInstance().notifyEvent(new OnInstanceDestroy(this), _template);
        }

        removePlayers();
        removeDoors();
        removeNpcs();

        InstanceManager.getInstance().unregister(getId());
    }

    /**
     * Teleport player out of instance.
     *
     * @param player player that should be moved out
     */
    public void ejectPlayer(Player player) {
        if (player.getInstanceWorld().equals(this)) {
            final Location loc = _template.getExitLocation(player);
            if (loc != null) {
                player.teleToLocation(loc, null);
            } else {
                player.teleToLocation(TeleportWhereType.TOWN, null);
            }
        }
    }

    /**
     * Send packet to each player from instance world.
     *
     * @param packets packets to be send
     */
    public void broadcastPacket(ServerPacket... packets) {
        for (Player player : _players) {
            for (ServerPacket packet : packets) {
                player.sendPacket(packet);
            }
        }
    }

    /**
     * Get remaining time before instance will be destroyed.
     *
     * @return remaining time in milliseconds if duration is not equal to -1, otherwise -1
     */
    public long getRemainingTime() {
        return (_endTime == -1) ? -1 : (_endTime - System.currentTimeMillis());
    }

    /**
     * Get instance destroy time.
     *
     * @return destroy time in milliseconds if duration is not equal to -1, otherwise -1
     */
    public long getEndTime() {
        return _endTime;
    }

    /**
     * Set reenter penalty for players associated with current instance.<br>
     * Penalty time is calculated from XML reenter data.
     */
    public void setReenterTime() {
        setReenterTime(_template.calculateReenterTime());
    }

    /**
     * Set reenter penalty for players associated with current instance.<br>
     *
     * @param time penalty time in milliseconds since January 1, 1970
     */
    public void setReenterTime(long time) {
        // Cannot store reenter data for instance without template id.
        if ((_template.getId() == -1) && (time > 0)) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT IGNORE INTO character_instance_time (charId,instanceId,time) VALUES (?,?,?)")) {
            // Save to database
            for (Player player : _allowed) {
                if (player != null) {
                    ps.setInt(1, player.getObjectId());
                    ps.setInt(2, _template.getId());
                    ps.setLong(3, time);
                    ps.addBatch();
                }
            }
            ps.executeBatch();

            // Save to memory and send message to player
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_POSSIBLE_ENTRY_TIME_BY_USING_THE_COMMAND_INSTANCEZONE);
            if (InstanceManager.getInstance().getInstanceName(getTemplateId()) != null) {
                msg.addInstanceName(_template.getId());
            } else {
                msg.addString(_template.getName());
            }
            _allowed.forEach(player ->
            {
                if (player != null) {
                    InstanceManager.getInstance().setReenterPenalty(player.getObjectId(), getTemplateId(), time);
                    if (player.isOnline()) {
                        player.sendPacket(msg);
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Could not insert character instance reenter data: ", e);
        }
    }

    /**
     * Set instance world to finish state.<br>
     * Calls method {@link Instance#finishInstance(int)} with {@link Config#INSTANCE_FINISH_TIME} as argument.<br>
     * See {@link Instance#finishInstance(int)} for more details.
     */
    public void finishInstance() {
        finishInstance(Config.INSTANCE_FINISH_TIME);
    }

    /**
     * Set instance world to finish state.<br>
     * Set re-enter for allowed players if required data are defined in template.<br>
     * Change duration of instance and set empty destroy time to 0 (instant effect).
     *
     * @param delay delay in minutes
     */
    public void finishInstance(int delay) {
        // Set re-enter for players
        if (_template.getReenterType() == InstanceReenterType.ON_FINISH) {
            setReenterTime();
        }
        // Change instance duration
        setDuration(delay);
    }

    // ---------------------------------------------
    // Listeners
    // ---------------------------------------------

    /**
     * This method is called when player dies inside instance.
     *
     * @param player
     */
    public void onDeath(Player player) {
        if (!player.isOnCustomEvent() && (_template.getEjectTime() > 0)) {
            // Send message
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.IF_YOU_ARE_NOT_RESURRECTED_WITHIN_S1_MINUTE_S_YOU_WILL_BE_EXPELLED_FROM_THE_INSTANT_ZONE);
            sm.addInt(_template.getEjectTime());
            player.sendPacket(sm);

            // Start eject task
            _ejectDeadTasks.put(player.getObjectId(), ThreadPool.schedule(() ->
            {
                if (player.isDead()) {
                    ejectPlayer(player.getActingPlayer());
                }
            }, _template.getEjectTime() * 60 * 1000)); // minutes to milliseconds
        }
    }

    /**
     * This method is called when player was resurrected inside instance.
     *
     * @param player resurrected player
     */
    public void doRevive(Player player) {
        final ScheduledFuture<?> task = _ejectDeadTasks.remove(player.getObjectId());
        if (task != null) {
            task.cancel(true);
        }
    }

    /**
     * This method is called when object enter or leave this instance.
     *
     * @param object instance of object which enters/leaves instance
     * @param enter  {@code true} when object enter, {@code false} when object leave
     */
    public void onInstanceChange(WorldObject object, boolean enter) {
        if (isPlayer(object)) {
            final Player player = object.getActingPlayer();
            if (enter) {
                addPlayer(player);

                // Set origin return location if enabled
                if (_template.getExitLocationType() == InstanceTeleportType.ORIGIN) {
                    player.getVariables().set("INSTANCE_ORIGIN", player.getX() + ";" + player.getY() + ";" + player.getZ());
                }

                // Remove player buffs
                if (_template.isRemoveBuffEnabled()) {
                    _template.removePlayerBuff(player);
                }

                // Notify DP scripts
                if (!isDynamic()) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnInstanceEnter(player, this), _template);
                }
            } else {
                removePlayer(player);
                // Notify DP scripts
                if (!isDynamic()) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnInstanceLeave(player, this), _template);
                }
            }
        } else if (isNpc(object)) {
            final Npc npc = (Npc) object;
            if (enter) {
                addNpc(npc);
            } else {
                if (npc.getSpawn() != null) {
                    npc.getSpawn().stopRespawn();
                }
                removeNpc(npc);
            }
        }
    }

    /**
     * This method is called when player logout inside instance world.
     *
     * @param player player who logout
     */
    public void onPlayerLogout(Player player) {
        removePlayer(player);
        if (Config.RESTORE_PLAYER_INSTANCE) {
            player.getVariables().set("INSTANCE_RESTORE", _id);
        } else {
            final Location loc = getExitLocation(player);
            if (loc != null) {
                player.setLocationInvisible(loc);
                // If player has death pet, put him out of instance world
                final Summon pet = player.getPet();
                if (pet != null) {
                    pet.teleToLocation(loc, true);
                }
            }
        }
    }


    /**
     * Get template ID of instance world.
     *
     * @return instance template ID
     */
    public int getTemplateId() {
        return _template.getId();
    }

    /**
     * Get type of re-enter data.
     *
     * @return type of re-enter (see {@link InstanceReenterType} for possible values)
     */
    public InstanceReenterType getReenterType() {
        return _template.getReenterType();
    }

    /**
     * Check if instance world is PvP zone.
     *
     * @return {@code true} when instance is PvP zone, otherwise {@code false}
     */
    public boolean isPvP() {
        return _template.isPvP();
    }

    /**
     * Check if summoning players to instance world is allowed.
     *
     * @return {@code true} when summon is allowed, otherwise {@code false}
     */
    public boolean isPlayerSummonAllowed() {
        return _template.isPlayerSummonAllowed();
    }

    /**
     * Get enter location for instance world.
     *
     * @return {@link Location} object if instance has enter location defined, otherwise {@code null}
     */
    public Location getEnterLocation() {
        return _template.getEnterLocation();
    }

    /**
     * Get all enter locations defined in XML template.
     *
     * @return list of enter locations
     */
    public List<Location> getEnterLocations() {
        return _template.getEnterLocations();
    }

    /**
     * Get exit location for player from instance world.
     *
     * @param player instance of player who wants to leave instance world
     * @return {@link Location} object if instance has exit location defined, otherwise {@code null}
     */
    public Location getExitLocation(Player player) {
        return _template.getExitLocation(player);
    }

    /**
     * @return the exp rate of the instance
     */
    public float getExpRate() {
        return _template.getExpRate();
    }

    /**
     * @return the sp rate of the instance
     */
    public float getSPRate() {
        return _template.getSPRate();
    }

    /**
     * @return the party exp rate of the instance
     */
    public float getExpPartyRate() {
        return _template.getExpPartyRate();
    }

    /**
     * @return the party sp rate of the instance
     */
    public float getSPPartyRate() {
        return _template.getSPPartyRate();
    }

    // ----------------------------------------------
    // Tasks
    // ----------------------------------------------

    /**
     * Clean up instance.
     */
    private void cleanUp() {
        if (getRemainingTime() <= TimeUnit.MINUTES.toMillis(1)) {
            sendWorldDestroyMessage(1);
            _cleanUpTask = ThreadPool.schedule(this::destroy, 60 * 1000); // 1 minute
        } else {
            sendWorldDestroyMessage(5);
            _cleanUpTask = ThreadPool.schedule(this::cleanUp, 5 * 60 * 1000); // 5 minutes
        }
    }

    /**
     * Show instance destroy messages to players inside instance world.
     *
     * @param delay time in minutes
     */
    private void sendWorldDestroyMessage(int delay) {
        // Dimensional wrap does not show timer after 5 minutes.
        if (delay > 5)
        {
            return;
        }
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THIS_INSTANT_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTE_S_YOU_WILL_BE_FORCED_OUT_OF_THE_DUNGEON_WHEN_THE_TIME_EXPIRES);
        sm.addInt(delay);
        broadcastPacket(sm);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Instance) && (((Instance) obj).getId() == getId());
    }

    @Override
    public String toString() {
        return _template.getName() + "(" + _id + ")";
    }


    /**
     * Handle open/close status of instance doors.
     * @param id ID of doors
     * @param open {@code true} means open door, {@code false} means close door
     */
    public void openCloseDoor(int id, boolean open)
    {
        final Door door = _doors.get(id);
        if (door != null)
        {
            if (open)
            {
                if (!door.isOpen())
                {
                    door.openMe();
                }
            }
            else if (door.isOpen())
            {
                door.closeMe();
            }
        }
    }

}