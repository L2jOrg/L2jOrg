/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.GroupType;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.enums.InstanceRemoveBuffType;
import org.l2j.gameserver.enums.InstanceTeleportType;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.AbstractPlayerGroup;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.holders.InstanceReenterTimeHolder;
import org.l2j.gameserver.model.instancezone.conditions.Condition;
import org.l2j.gameserver.model.instancezone.conditions.ConditionCommandChannel;
import org.l2j.gameserver.model.instancezone.conditions.ConditionGroupMax;
import org.l2j.gameserver.model.instancezone.conditions.ConditionGroupMin;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.spawns.SpawnTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Template holder for instances.
 *
 * @author malyelfik
 * @author JoeAlisson
 */
public class InstanceTemplate extends ListenersContainer implements IIdentifiable, INamable {

    private final IntMap<DoorTemplate> doors = new HashIntMap<>();
    private final List<SpawnTemplate> spawns = new ArrayList<>();

    private final int id;
    private final String name;
    private final int maxWorldCount;

    private int duration = -1;
    private long emptyDestroyTime = -1;
    private int ejectTime = Config.EJECT_DEAD_PLAYER_TIME;
    private boolean isPvP = false;
    private boolean allowPlayerSummon = false;
    private float expRate = Config.RATE_INSTANCE_XP;
    private float spRate = Config.RATE_INSTANCE_SP;
    private float expPartyRate = Config.RATE_INSTANCE_PARTY_XP;
    private float spPartyRate = Config.RATE_INSTANCE_PARTY_SP;
    private StatsSet parameters = StatsSet.EMPTY_STATSET;

    private InstanceTeleportType enterLocationType = InstanceTeleportType.NONE;
    private List<Location> enterLocations = null;
    private InstanceTeleportType exitLocationType = InstanceTeleportType.NONE;
    private List<Location> exitLocations = null;

    private InstanceReenterType reenterType = InstanceReenterType.NONE;
    private List<InstanceReenterTimeHolder> reenterData = Collections.emptyList();

    private InstanceRemoveBuffType removeBuffType = InstanceRemoveBuffType.NONE;
    private IntSet removeBuffExceptions = Containers.emptyIntSet();

    private List<Condition> conditions = Collections.emptyList();
    private int groupMask = GroupType.NONE.getMask();

    public InstanceTemplate() {
        id = 0;
        name = null;
        maxWorldCount = -1;
    }

    public InstanceTemplate(int id, String name, int maxWorld) {
        this.id = id;
        this.name = name;
        maxWorldCount = maxWorld;
    }

    /**
     * Allow summoning players (that are out of instance) to instance world by players inside.
     *
     * @param val {@code true} means summon is allowed, {@code false} means summon is prohibited
     */
    public void allowPlayerSummon(boolean val) {
        allowPlayerSummon = val;
    }

    /**
     * Set instance as PvP world.
     *
     * @param val {@code true} world is PvP zone, {@code false} world use classic zones
     */
    public void setIsPvP(boolean val) {
        isPvP = val;
    }

    /**
     * Add door into instance world.
     *
     * @param templateId template id of door
     * @param template   door template
     */
    public void addDoor(int templateId, DoorTemplate template) {
        doors.put(templateId, template);
    }

    /**
     * Add new group of NPC spawns into instance world.<br>
     * Group with name "general" will be spawned on instance world create.
     *
     * @param spawns list of NPC spawn data
     */
    public void addSpawns(List<SpawnTemplate> spawns) {
        this.spawns.addAll(spawns);
    }

    /**
     * Set enter locations for instance world.
     *
     * @param type      type of teleport ({@link InstanceTeleportType#FIXED} or {@link InstanceTeleportType#RANDOM} are supported)
     * @param locations list of locations used for determining final enter location
     */
    public void setEnterLocation(InstanceTeleportType type, List<Location> locations) {
        enterLocationType = type;
        enterLocations = locations;
    }

    /**
     * Set exit locations for instance world.
     *
     * @param type      type of teleport (see {@link InstanceTeleportType} for all possible types)
     * @param locations list of locations used for determining final exit location
     */
    public void setExitLocation(InstanceTeleportType type, List<Location> locations) {
        exitLocationType = type;
        exitLocations = locations;
    }

    /**
     * Set re-enter data for instance world.<br>
     * This method also enable re-enter condition for instance world.
     *
     * @param type   reenter type means when reenter restriction should be applied (see {@link InstanceReenterType} for more info)
     * @param holder data which are used to calculate reenter time
     */
    public void setReenterData(InstanceReenterType type, List<InstanceReenterTimeHolder> holder) {
        reenterType = type;
        reenterData = holder;
    }

    /**
     * Set remove buff list for instance world.<br>
     * These data are used to restrict player buffs when he enters into instance.
     *
     * @param type          type of list like blacklist, whitelist, ... (see {@link InstanceRemoveBuffType} for more info)
     * @param exceptionList
     */
    public void setRemoveBuff(InstanceRemoveBuffType type, IntSet exceptionList) {
        removeBuffType = type;
        removeBuffExceptions = exceptionList;
    }

    /**
     * Register conditions to instance world.<br>
     * This method also set new enter group mask according to given conditions.
     *
     * @param conditions list of conditions
     */
    public void setConditions(List<Condition> conditions) {
        // Set conditions
        this.conditions = conditions;

        // Now iterate over conditions and determine enter group data
        boolean onlyCC = false;
        int min = 1;
        int max = 1;
        for (Condition cond : this.conditions) {
            if (cond instanceof ConditionCommandChannel) {
                onlyCC = true;
            } else if (cond instanceof ConditionGroupMin) {
                min = ((ConditionGroupMin) cond).getLimit();
            } else if (cond instanceof ConditionGroupMax) {
                max = ((ConditionGroupMax) cond).getLimit();
            }
        }

        // Reset group mask before setting new group
        groupMask = 0;
        // Check if player can enter in other group then Command channel
        if (!onlyCC) {
            // Player
            if (min == 1) {
                groupMask |= GroupType.NONE.getMask();
            }
            // Party
            final int partySize = Config.ALT_PARTY_MAX_MEMBERS;
            if (((max > 1) && (max <= partySize)) || ((min <= partySize) && (max > partySize))) {
                groupMask |= GroupType.PARTY.getMask();
            }
        }
        // Command channel
        if (onlyCC || (max > 7)) {
            groupMask |= GroupType.COMMAND_CHANNEL.getMask();
        }
    }

    // -------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get all enter locations defined in XML template.
     *
     * @return list of enter locations
     */
    public List<Location> getEnterLocations() {
        return enterLocations;
    }

    /**
     * Get enter location to instance world.
     *
     * @return enter location if instance has any, otherwise {@code null}
     */
    public Location getEnterLocation() {
        Location loc = null;
        switch (enterLocationType) {
            case RANDOM: {
                loc = enterLocations.get(Rnd.get(enterLocations.size()));
                break;
            }
            case FIXED: {
                loc = enterLocations.get(0);
                break;
            }
        }
        return loc;
    }

    /**
     * Get type of exit location.
     *
     * @return exit location type (see {@link InstanceTeleportType} for possible values)
     */
    public InstanceTeleportType getExitLocationType() {
        return exitLocationType;
    }

    List<Location> getExitLocations() {
        return exitLocations;
    }

    /**
     * Get time after empty instance is destroyed.
     *
     * @return time in milliseconds
     */
    public long getEmptyDestroyTime() {
        return emptyDestroyTime;
    }

    /**
     * Set time after empty instance will be destroyed.
     *
     * @param emptyDestroyTime time in minutes
     */
    public void setEmptyDestroyTime(long emptyDestroyTime) {
        if (emptyDestroyTime >= 0) {
            this.emptyDestroyTime = TimeUnit.MINUTES.toMillis(emptyDestroyTime);
        }
    }

    /**
     * Get instance duration time.
     *
     * @return time in minutes
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set instance world duration.
     *
     * @param duration time in minutes
     */
    public void setDuration(int duration) {
        if (duration > 0) {
            this.duration = duration;
        }
    }

    /**
     * Get time after dead player is ejected from instance world.
     *
     * @return time in minutes
     */
    public int getEjectTime() {
        return ejectTime;
    }

    /**
     * Set time after death player will be ejected from instance world.<br>
     * Default: {@link Config#EJECT_DEAD_PLAYER_TIME}
     *
     * @param ejectTime time in minutes
     */
    public void setEjectTime(int ejectTime) {
        if (ejectTime >= 0) {
            this.ejectTime = ejectTime;
        }
    }

    /**
     * Check if summoning player into instance is allowed.
     *
     * @return {@code true} if summon is allowed, otherwise {@code false}
     */
    public boolean isPlayerSummonAllowed() {
        return allowPlayerSummon;
    }

    /**
     * Check if instance is PvP zone.
     *
     * @return {@code true} if instance is PvP, otherwise {@code false}
     */
    public boolean isPvP() {
        return isPvP;
    }

    /**
     * Get doors data for instance world.
     *
     * @return map in form <i>doorId, door template</i>
     */
    public IntMap<DoorTemplate> getDoors() {
        return doors;
    }

    /**
     * @return list of all spawn templates
     */
    public List<SpawnTemplate> getSpawns() {
        return spawns;
    }

    /**
     * Get count of instance worlds which can run concurrently with same template ID.
     *
     * @return count of worlds
     */
    public int getMaxWorlds() {
        return maxWorldCount;
    }

    /**
     * Get instance template parameters.
     *
     * @return parameters of template
     */
    public StatsSet getParameters() {
        return parameters;
    }

    /**
     * Set parameters shared between instances with same template id.
     *
     * @param set map containing parameters
     */
    public void setParameters(Map<String, Object> set) {
        if (!set.isEmpty()) {
            parameters = new StatsSet(Collections.unmodifiableMap(set));
        }
    }

    /**
     * Check if buffs are removed upon instance enter.
     *
     * @return {@code true} if any buffs should be removed, otherwise {@code false}
     */
    public boolean isRemoveBuffEnabled() {
        return removeBuffType != InstanceRemoveBuffType.NONE;
    }

    /**
     * Remove buffs from player according to remove buff data
     *
     * @param player player which loose buffs
     */
    public void removePlayerBuff(Player player) {
        // Make list of affected playable objects
        final List<Playable> affected = new ArrayList<>();
        affected.add(player);
        player.getServitors().values().forEach(affected::add);
        if (player.hasPet()) {
            affected.add(player.getPet());
        }

        // Now remove buffs by type
        if (removeBuffType == InstanceRemoveBuffType.ALL) {
            for (Playable playable : affected) {
                playable.stopAllEffectsExceptThoseThatLastThroughDeath();
            }
        } else {
            for (Playable playable : affected) {
                // Stop all buffs.
                playable.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && info.getSkill().getBuffType().isBuff() && hasRemoveBuffException(info.getSkill()), true, true);
            }
        }
    }

    /**
     * Check if given buff {@code skill} should be removed.
     *
     * @param skill buff which should be removed
     * @return {@code true} if buff will be removed, otherwise {@code false}
     */
    private boolean hasRemoveBuffException(Skill skill) {
        final boolean containsSkill = removeBuffExceptions.contains(skill.getId());
        return (removeBuffType == InstanceRemoveBuffType.BLACKLIST) ? containsSkill : !containsSkill;
    }

    /**
     * Get type of re-enter data.
     *
     * @return type of re-enter (see {@link InstanceReenterType} for possible values)
     */
    public InstanceReenterType getReenterType() {
        return reenterType;
    }

    /**
     * Calculate re-enter time for instance world.
     *
     * @return re-enter time in milliseconds
     */
    public long calculateReenterTime() {
        long time = -1;
        for (InstanceReenterTimeHolder data : reenterData) {
            if (data.getTime() > 0) {
                time = System.currentTimeMillis() + data.getTime();
                break;
            }

            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, data.getHour());
            calendar.set(Calendar.MINUTE, data.getMinute());
            calendar.set(Calendar.SECOND, 0);

            // If calendar time is lower than current, add one more day
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Modify calendar day
            if (data.getDay() != null) {
                // DayOfWeek starts with Monday(1) but Calendar starts with Sunday(1)
                int day = data.getDay().getValue() + 1;
                if (day > 7) {
                    day = 1;
                }

                // Set exact day. If modified date is before current, add one more week.
                calendar.set(Calendar.DAY_OF_WEEK, day);
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_MONTH, 1);
                }
            }

            if ((time == -1) || (calendar.getTimeInMillis() < time)) {
                time = calendar.getTimeInMillis();
            }
        }
        return time;
    }

    /**
     * Check if enter group mask contains given group type {@code type}.
     *
     * @param type type of group
     * @return {@code true} if mask contains given group, otherwise {@code false}
     */
    private final boolean groupMaskContains(GroupType type) {
        final int flag = type.getMask();
        return (groupMask & flag) == flag;
    }

    /**
     * Get enter group which can enter into instance world based on player's group.
     *
     * @param player player who wants to enter
     * @return group type which can enter if any can enter, otherwise {@code null}
     */
    private GroupType getEnterGroupType(Player player) {
        // If mask doesn't contain any group
        if (groupMask == 0) {
            return null;
        }

        // If player can override instance conditions then he can enter alone
        if (player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS)) {
            return GroupType.NONE;
        }

        // Check if mask contains player's group
        final GroupType playerGroup = player.getGroupType();
        if (groupMaskContains(playerGroup)) {
            return playerGroup;
        }

        // Check if mask contains only one group
        final GroupType type = GroupType.getByMask(groupMask);
        if (type != null) {
            return type;
        }

        // When mask contains more group types but without player's group, choose nearest one
        // player < party < command channel
        for (GroupType t : GroupType.values()) {
            if (t != playerGroup && groupMaskContains(t)) {
                return t;
            }
        }
        // nothing found? then player cannot enter
        return null;
    }

    /**
     * Get player's group based on result of {@link InstanceTemplate#getEnterGroupType(Player)}.
     *
     * @param player player who wants to enter into instance
     * @return list of players (first player in list is player who make enter request)
     */
    public List<Player> getEnterGroup(Player player) {
        final GroupType type = getEnterGroupType(player);
        if (type == null) {
            return null;
        }

        // Make list of players which can enter into instance world
        final List<Player> group = new ArrayList<>();
        group.add(player); // Put player who made request at first position inside list

        // Check if player has group in which he can enter
        AbstractPlayerGroup pGroup = null;
        if (type == GroupType.PARTY) {
            pGroup = player.getParty();
        } else if (type == GroupType.COMMAND_CHANNEL) {
            pGroup = player.getCommandChannel();
        }

        // If any group found then put them into enter group list
        if (pGroup != null) {
            pGroup.getMembers().stream().filter(p -> !p.equals(player)).forEach(group::add);
        }
        return group;
    }

    /**
     * Validate instance conditions for given group.
     *
     * @param group        group of players which want to enter instance world
     * @param npc          instance of NPC used to enter to instance
     * @param htmlCallback callback function used to display fail HTML when condition validate failed
     * @return {@code true} when all condition are met, otherwise {@code false}
     */
    public boolean validateConditions(List<Player> group, Npc npc, BiConsumer<Player, String> htmlCallback) {
        for (Condition cond : conditions) {
            if (!cond.validate(npc, group, htmlCallback)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply condition effects for each player from enter group.
     *
     * @param group players from enter group
     */
    public void applyConditionEffects(List<Player> group) {
        conditions.forEach(c -> c.applyEffect(group));
    }

    /**
     * @return the exp rate of the instance
     **/
    public float getExpRate() {
        return expRate;
    }

    /**
     * Sets the exp rate of the instance
     *
     * @param expRate
     **/
    public void setExpRate(float expRate) {
        this.expRate = expRate;
    }

    /**
     * @return the sp rate of the instance
     */
    public float getSPRate() {
        return spRate;
    }

    /**
     * Sets the sp rate of the instance
     *
     * @param spRate
     **/
    public void setSPRate(float spRate) {
        this.spRate = spRate;
    }

    /**
     * @return the party exp rate of the instance
     */
    public float getExpPartyRate() {
        return expPartyRate;
    }

    /**
     * Sets the party exp rate of the instance
     *
     * @param expRate
     **/
    public void setExpPartyRate(float expRate) {
        expPartyRate = expRate;
    }

    /**
     * @return the party sp rate of the instance
     */
    public float getSPPartyRate() {
        return spPartyRate;
    }

    /**
     * Sets the party sp rate of the instance
     *
     * @param spRate
     **/
    public void setSPPartyRate(float spRate) {
        spPartyRate = spRate;
    }

    /**
     * Get count of created instance worlds.
     *
     * @return count of created instances
     */
    public long getWorldCount() {
        return InstanceManager.getInstance().getWorldCount(getId());
    }

    @Override
    public String toString() {
        return "ID: " + id + " Name: " + name;
    }
}