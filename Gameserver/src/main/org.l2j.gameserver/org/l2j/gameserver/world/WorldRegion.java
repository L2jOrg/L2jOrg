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
package org.l2j.gameserver.world;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.taskmanager.RandomAnimationTaskManager;
import org.l2j.gameserver.util.MathUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public final class WorldRegion {

    private final IntMap<WorldObject> objects = new CHashIntMap<>();
    private final Object taskLocker = new Object();
    private final int regionX;
    private final int regionY;

    private WorldRegion[] surroundingRegions;
    private ScheduledFuture<?> neighborsTask = null;
    private boolean active;
    private final AtomicInteger playersInside = new AtomicInteger(0);

    WorldRegion(int regionX, int regionY) {
        this.regionX = regionX;
        this.regionY = regionY;
    }

    public void addVisibleObject(WorldObject object) {
        if (isNull(object)) {
            return;
        }

        if (isNull(objects.put(object.getObjectId(), object)) && isPlayer(object)) {
            // If this is the first player to enter the region, activate self and neighbors.
            playersInside.getAndIncrement();
            if (!active) {
                startActivation();
            }
        }
    }

    /**
     * Immediately sets self as active and starts a timer to set neighbors as active this timer is to avoid turning on neighbors in the case when a person just teleported into a region and then teleported out immediately...there is no reason to activate all the neighbors in that case.
     */
    private void startActivation() {
        setActive(true);
        startNeighborsTask(true, Config.GRID_NEIGHBOR_TURNON_TIME);
    }

    private void setActive(boolean active) {
        if (this.active == active) {
            return;
        }

        this.active = active;

        switchAI();
    }

    private void switchAI() {
        if (objects.isEmpty()) {
            return;
        }

        if (!active) {
            for (WorldObject object : objects.values()) {
                if (object instanceof Attackable attackable) {
                    // Set target to null and cancel attack or cast.
                    attackable.setTarget(null);

                    // Stop movement.
                    attackable.stopMove(null);

                    // Stop all active skills effects in progress on the Creature.
                    attackable.stopAllEffects();

                    attackable.clearAggroList();
                    attackable.getAttackByList().clear();

                    // Stop the AI tasks.
                    if (attackable.hasAI()) {
                        attackable.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                        attackable.getAI().stopAITask();
                    }
                    RandomAnimationTaskManager.getInstance().remove(attackable);
                } else if (isNpc(object)) {
                    RandomAnimationTaskManager.getInstance().remove((Npc) object);
                }
            }
        } else {
            for (WorldObject object : objects.values()) {
                if (object instanceof Attackable attackable) {
                    attackable.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    RandomAnimationTaskManager.getInstance().add(attackable);
                } else if (isNpc(object)) {
                    RandomAnimationTaskManager.getInstance().add((Npc) object);
                }
            }
        }
    }

    private void startNeighborsTask(boolean activating, int taskStartTime) {
        synchronized (taskLocker) {
            if (nonNull(neighborsTask)) {
                neighborsTask.cancel(true);
                neighborsTask = null;
            }

            // Then, set a timer to activate the neighbors.
            neighborsTask = ThreadPool.schedule(new NeighborsTask(activating), 1000 * taskStartTime);
        }
    }

    private void startDeactivation() {
        startNeighborsTask(false, Config.GRID_NEIGHBOR_TURNOFF_TIME);
    }

    void removeVisibleObject(WorldObject object) {
        if (isNull(object) || objects.isEmpty()) {
            return;
        }

        if (nonNull(objects.remove(object.getObjectId())) && isPlayer(object)) {
            playersInside.getAndDecrement();
            if (areNeighborsEmpty()) {
                startDeactivation();
            }
        }
    }

    private boolean areNeighborsEmpty() {
        for (WorldRegion region : surroundingRegions) {
            if(region.isActive() && region.playersInside.get() > 0) {
                return false;
            }
        }
        return true;
    }

    <T extends WorldObject> void forEachObjectInSurrounding(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        T casted;
        for (WorldRegion region : surroundingRegions) {
            for (WorldObject object : region.objects.values()) {
                if(clazz.isInstance(object) && filter.test(casted = clazz.cast(object) )){
                    action.accept(casted);
                }
            }
        }
    }

    <T extends WorldObject> void forEachObjectInSurroundingLimiting(Class<T> clazz, int limit, Predicate<T> filter, Consumer<T> action) {
        T casted;
        int accepted = 0;
        for (WorldRegion region : surroundingRegions) {
            for (WorldObject object : region.objects.values()) {
                if(clazz.isInstance(object) && filter.test(casted = clazz.cast(object) )){
                    action.accept(casted);
                    if(++accepted > limit) {
                        return;
                    }
                }
            }
        }
    }

    <T extends WorldObject> void forEachOrderedObjectInSurrounding(Class<T> clazz, int maxObjects, Comparator<T> comparator, Predicate<T> filter, Consumer<? super T> action) {
        filteredSurroundingObjects(clazz, filter).sorted(comparator).limit(maxObjects).forEach(action);
    }

    private <T extends WorldObject> Stream<T> filteredSurroundingObjects(Class<T> clazz, Predicate<T> filter) {
        return Arrays.stream(surroundingRegions).flatMap(r -> r.objects.values().stream()).filter(o -> applyInstanceFilter(o, clazz, filter)).map(clazz::cast);
    }

    <T extends WorldObject> void forAnyObjectInSurrounding(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        for (WorldRegion region : surroundingRegions) {
            for (WorldObject object : region.objects.values()) {
                if(applyInstanceFilter(object, clazz, filter)) {
                    action.accept(clazz.cast(object));
                    return;
                }
            }
        }
    }

    WorldObject findObjectInSurrounding(WorldObject reference, int objectId, int range) {
        WorldObject object;
        for(var region : surroundingRegions) {
            if( nonNull(object = region.getObject(objectId)) ) {
                if(!MathUtil.isInsideRadius3D(reference, object, range)) {
                    return null;
                }
                return object;
            }
        }
        return null;
    }

    <T extends WorldObject> List<T> findAllObjectsInSurrounding(Class<T> clazz, Predicate<T> filter) {
        return filteredSurroundingObjects(clazz, filter).collect(Collectors.toList());
    }

    <T extends WorldObject> T findAnyObjectInSurrounding(Class<T> clazz, Predicate<T> filter) {
        for (WorldRegion region : surroundingRegions) {
            for (WorldObject object : region.objects.values()) {
                if(applyInstanceFilter(object, clazz, filter)) {
                    return clazz.cast(object);
                }
            }
        }
        return null;
    }

    <T extends WorldObject> T findFirstObjectInSurrounding(Class<T> clazz, Predicate<T> filter, Comparator<T> comparator) {
        return filteredSurroundingObjects(clazz, filter).min(comparator).orElse(null);
    }

    <T extends WorldObject> boolean hasObjectInSurrounding(Class<T> clazz, Predicate<T> filter) {
        for (WorldRegion region : surroundingRegions) {
            for (WorldObject object : region.objects.values()) {
                if(applyInstanceFilter(object, clazz, filter)) {
                    return true;
                }
            }
        }
        return false;
    }

    WorldObject getObject(int objectId) {
        return objects.get(objectId);
    }

    void setSurroundingRegions(WorldRegion[] regions) {
        surroundingRegions = regions;

        // Make sure that this region is always the first region to improve bulk operations when this region should be update first
        for (int i = 0; i < surroundingRegions.length; i++) {
            if(surroundingRegions[i] == this) {
                var first = surroundingRegions[0];
                surroundingRegions[0] = this;
                surroundingRegions[i] = first;
            }
        }
    }

    public boolean isInSurroundingRegion(WorldObject object) {
        WorldRegion objectRegion;
        if(isNull(object) || isNull(objectRegion = object.getWorldRegion())) {
            return false;
        }
        return isSurroundingRegion(objectRegion);
    }

    boolean isSurroundingRegion(WorldRegion region) {
        return nonNull(region) && abs(regionX - region.regionX) <= 1 && abs(regionY - region.regionY) <= 1;
    }

    public boolean isActive() {
        return active;
    }


    private static <T extends WorldObject> boolean applyInstanceFilter(WorldObject object, Class<T> clazz, Predicate<T> filter) {
        return clazz.isInstance(object) && filter.test(clazz.cast(object));
    }

    @Override
    public String toString() {
        return "[" + regionX + ", " + regionY + "]";
    }

    WorldRegion[] surroundingRegions() {
        return surroundingRegions;
    }

    Collection<WorldObject> objects() {
        return objects.values();
    }

    public int getPlayersCountInSurround() {
        int playersCount = 0;
        for (WorldRegion surroundingRegion : surroundingRegions) {
            playersCount += surroundingRegion.playersInside.get();
        }
        return playersCount;
    }

    private class NeighborsTask implements Runnable {
        private final boolean isActivating;

        NeighborsTask(boolean isActivating) {
            this.isActivating = isActivating;
        }

        @Override
        public void run() {
            for (WorldRegion region : surroundingRegions) {
                if(isActivating || region.areNeighborsEmpty()) {
                    region.setActive(isActivating);
                }
            }
        }
    }
}
