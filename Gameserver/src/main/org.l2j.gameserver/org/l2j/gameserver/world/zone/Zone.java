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
package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Abstract base class for any zone type handles basic operations.
 *
 * @author durgus
 * @author JoeAlisson
 */
public abstract class Zone extends ListenersContainer {

    private final int id;
    private boolean enabled;

    private String name = null;
    private AbstractZoneSettings settings;

    protected ZoneArea area;
    protected IntMap<Creature> creatures = new CHashIntMap<>();

    protected Zone(int id) {
        this.id = id;
        enabled = true;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }
    
    /**
     * @param creature the creature to verify.
     * @return {@code true} if the given character is affected by this zone, {@code false} otherwise.
     */
    protected boolean isAffected(Creature creature) {
        return isEnabled();
    }

    /**
     *
     * @return if the given coordinates are within the zone, ignores instanceId check
     */
    public boolean isInsideZone(int x, int y, int z) {
        return area.isInside(x, y, z);
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
        return area.distanceFrom(object.getX(), object.getY());
    }

    protected void revalidateInZone(Creature creature) {
        if (isInsideZone(creature)) {
            if (!isAffected(creature)) {
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
        for (Creature creature : creatures.values()) {
            if(filter.test(creature)) {
                action.accept(creature);
            }
        }
    }

    public void forAnyCreature(Consumer<Creature> action, Predicate<Creature> filter) {
        for (Creature creature : creatures.values()) {
            if(filter.test(creature)) {
                action.accept(creature);
                return;
            }
        }
    }

    public void forEachPlayer(Consumer<Player> action, Predicate<Player> filter) {
        for (Creature creature : creatures.values()) {
            if(creature instanceof Player player && filter.test(player) ) {
                action.accept(player);
            }
        }
    }

    public void forEachPlayer(Consumer<Player> action) {
        for (Creature creature : creatures.values()) {
            if(creature instanceof Player player) {
                action.accept(player);
            }
        }
    }

    public long getPlayersInsideCount() {
        var count = 0L;
        for (Creature creature : creatures.values()) {
            if(creature instanceof Player) {
                count++;
            }
        }
        return count;
    }

    /**
     * Broadcasts packet to all players inside the zone
     *
     */
    public void broadcastPacket(ServerPacket packet) {
        if (creatures.isEmpty()) {
            return;
        }

        for (Creature creature : creatures.values()) {
            if(creature instanceof Player player) {
                packet.sendTo(player);
            }
        }
    }

    public void visualizeZone(Player player) {
        area.visualize(player, toString());
    }

    public void oustAllPlayers() {
        if(creatures.isEmpty()) {
            return;
        }
        for (Creature creature : creatures.values()) {
            if(creature instanceof Player) {
                creature.teleToLocation(TeleportWhereType.TOWN);
            }
        }
    }

    public void movePlayersTo(Location loc) {
        if (creatures.isEmpty()) {
            return;
        }
        for (Creature creature : creatures.values()) {
            if(isPlayer(creature)) {
                creature.teleToLocation(loc);
            }
        }
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

    public boolean isEmpty() {
        return creatures.isEmpty();
    }

    protected abstract void onEnter(Creature creature);
    protected abstract void onExit(Creature creature);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + "] - " + name;
    }
}