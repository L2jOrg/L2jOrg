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
package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Abstract base class for any zone type handles basic operations.
 *
 * @author durgus
 * @author JoeAlisson
 */
public abstract class Zone extends ListenersContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Zone.class);
    public static final int FIGHTER = 1;
    public static final int MAGE = 2;

    private final int id;
    private boolean enabled;
    /**
     * Parameters to affect specific characters
     */
    private boolean checkAffected = false;
    private String name = null;
    private int minLvl;
    private int maxLvl;
    private int[] races;
    private int[] classes;
    private char classType;
    private InstanceType target = InstanceType.Creature; // default all chars
    private boolean allowStore;
    private AbstractZoneSettings settings;
    private int instanceTemplateId;

    protected ZoneArea area;
    protected IntMap<Creature> creatures = new CHashIntMap<>();

    protected Zone(int id) {
        this.id = id;
        maxLvl = 0xFF;

        allowStore = true;
        enabled = true;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    public void setParameter(String name, String value) {
        checkAffected = true;

        switch (name) {
            default -> LOGGER.warn("Unknown parameter - {} in zone: {}", name, id);
            case "name" -> this.name = value;
            case "affectedLvlMin" -> minLvl = Integer.parseInt(value);
            case "affectedLvlMax" -> maxLvl = Integer.parseInt(value);
            case "affectedClassType" -> classType = (char) (value.equalsIgnoreCase("Fighter") ? FIGHTER : MAGE);
            case "targetClass" -> target = Enum.valueOf(InstanceType.class, value);
            case "allowStore"-> allowStore = Boolean.parseBoolean(value);
            case "default_enabled" -> enabled = Boolean.parseBoolean(value);
            case "instanceId" -> instanceTemplateId = Integer.parseInt(value);
            case "affectedRace" -> {
                if (isNull(races)) {
                    races = new int[1];
                    races[0] = Integer.parseInt(value);
                } else {
                    races = Arrays.copyOf(races, races.length + 1);
                    races[races.length - 1] = Integer.parseInt(value);
                }
            }
            case "affectedClassId" -> {
                if (isNull(classes)) {
                    classes = new int[1];
                    classes[0] = Integer.parseInt(value);
                } else {
                    classes = Arrays.copyOf(classes, classes.length + 1);
                    classes[classes.length - 1] = Integer.parseInt(value);
                }
            }
        }
    }

    /**
     * @param creature the player to verify.
     * @return {@code true} if the given character is affected by this zone, {@code false} otherwise.
     */
    private boolean isAffected(Creature creature) {
        if(!isEnabled()) {
            return false;
        }
        final Instance instance = creature.getInstanceWorld();

        if (nonNull(instance)) {
            if (instance.getTemplateId() != instanceTemplateId) {
                return false;
            }
        } else if (instanceTemplateId > 0) {
            return false;
        }

        if (creature.getLevel() < minLvl || creature.getLevel() > maxLvl) {
            return false;
        }

        if (!creature.isInstanceTypes(target)) {
            return false;
        }

        if (isPlayer(creature)) {
            var isMage = ((Player) creature).isMageClass();
            if( (isMage && classType == FIGHTER) || (!isMage && classType == MAGE) ) {
                return false;
            }

            if(nonNull(races)) {
                if(Arrays.stream(races).noneMatch(id -> id == creature.getRace().ordinal())) {
                    return false;
                }
            }

            if(nonNull(classes)) {
                return Arrays.stream(classes).anyMatch(id -> id == ((Player) creature).getClassId().getId());
            }
        }
        return true;
    }

    /**
     *
     * @return if the given coordinates are within the zone, ignores instanceId check
     */
    public boolean isInsideZone(int x, int y, int z) {
        return area.isInsideZone(x, y, z);
    }

    /**
     * @return if the given coordinates are within zone's plane
     */
    public boolean isInsideZone(int x, int y) {
        return isInsideZone(x, y, area.getHighZ());
    }

    /**
     *
     * @return if the given location are within the zone, ignores instanceId check
     */
    public boolean isInsideZone(ILocational loc) {
        return isInsideZone(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     *
     * @return if the given object is inside the zone.
     */
    public boolean isInsideZone(WorldObject object) {
        return isInsideZone(object.getX(), object.getY(), object.getZ());
    }

    public double getDistanceToZone(WorldObject object) {
        return area.getDistanceToZone(object.getX(), object.getY());
    }

    protected void revalidateInZone(Creature creature) {
        if (isInsideZone(creature)) {
            if (checkAffected && !isAffected(creature)) {
                return;
            }

            if (creatures.putIfAbsent(creature.getObjectId(), creature) == null) {
                onEnter(creature);
                EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneEnter(creature, this), this);
            }
        } else {
            removeCreature(creature);
        }
    }

    /**
     * Force fully removes a character from the zone Should use during teleport / logoff
     */
    protected void removeCreature(Creature creature) {
        if (creatures.containsKey(creature.getObjectId())) {
            creatures.remove(creature.getObjectId());
            onExit(creature);
            EventDispatcher.getInstance().notifyEventAsync(new OnCreatureZoneExit(creature, this), this);
        }
    }

    /**
     * @return if creature is inside zone
     */
    public boolean isCreatureInZone(Creature creature) {
        return creatures.containsKey(creature.getObjectId());
    }

    protected void onDieInside(Creature creature) {
    }

    protected void onReviveInside(Creature creature) {
    }

    public void onPlayerLoginInside(Player player) {
    }

    public void onPlayerLogoutInside(Player player) {
    }

    public void forEachCreature(Consumer<Creature> action) {
        creatures.values().forEach(action);
    }

    public void forEachCreature(Consumer<Creature> action, Predicate<Creature> filter) {
        creatures.values().stream().filter(filter).forEach(action);
    }

    public void forAnyCreature(Consumer<Creature> action, Predicate<Creature> filter) {
        creatures.values().stream().filter(filter).findAny().ifPresent(action);
    }

    public void forEachPlayer(Consumer<Player> action, Predicate<Player> filter) {
        toPlayerStream(creatures.values().stream()).filter(filter).forEach(action);
    }

    public void forEachPlayer(Consumer<Player> action) {
        toPlayerStream(creatures.values().stream()).forEach(action);
    }

    public long getPlayersInsideCount() {
        return creatures.values().stream().filter(GameUtils::isPlayer).count();
    }

    /**
     * Broadcasts packet to all players inside the zone
     *
     */
    public void broadcastPacket(ServerPacket packet) {
        if (creatures.isEmpty()) {
            return;
        }
       toPlayerStream(creatures.values().parallelStream()).forEach(packet::sendTo);
    }

    private Stream<Player> toPlayerStream(Stream<Creature> stream) {
        return  stream.filter(GameUtils::isPlayer).map(WorldObject::getActingPlayer);
    }

    public void visualizeZone(int z) {
        area.visualizeZone(z);
    }

    public void oustAllPlayers() {
        if(creatures.isEmpty()) {
            return;
        }

        toPlayerStream(creatures.values().parallelStream()).forEach(player -> player.teleToLocation(TeleportWhereType.TOWN));
    }

    public void movePlayersTo(Location loc) {
        if (creatures.isEmpty()) {
            return;
        }

        toPlayerStream(creatures.values().parallelStream()).forEach(p -> p.teleToLocation(loc));
    }

    public ZoneArea getArea() {
        return area;
    }

    public void setArea(ZoneArea area) {
        if (this.area != null) {
            throw new IllegalStateException("Zone already set");
        }
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractZoneSettings getSettings() {
        return settings;
    }

    public void setSettings(AbstractZoneSettings settings) {
        if (this.settings != null) {
            this.settings.clear();
        }
        this.settings = settings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean state) {
        enabled = state;
    }

    public InstanceType getTargetType() {
        return target;
    }

    public void setTargetType(InstanceType type) {
        target = type;
        checkAffected = true;
    }

    protected boolean getAllowStore() {
        return allowStore;
    }

    public int getInstanceTemplateId() {
        return instanceTemplateId;
    }

    protected abstract void onEnter(Creature character);
    protected abstract void onExit(Creature character);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "] - " + name;
    }
}