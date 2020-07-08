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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.*;

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

    WorldRegion(int regionX, int regionY) {
        this.regionX = regionX;
        this.regionY = regionY;
    }

    public void addVisibleObject(WorldObject object) {
        if (isNull(object)) {
            return;
        }

        objects.put(object.getObjectId(), object);

        if (isPlayable(object)) {
            // If this is the first player to enter the region, activate self and neighbors.
            if (!active && (!Config.GRIDS_ALWAYS_ON)) {
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
            for (WorldObject o : objects.values()) {
                if (isAttackable(o)) {
                    final Attackable mob = (Attackable) o;

                    // Set target to null and cancel attack or cast.
                    mob.setTarget(null);

                    // Stop movement.
                    mob.stopMove(null);

                    // Stop all active skills effects in progress on the Creature.
                    mob.stopAllEffects();

                    mob.clearAggroList();
                    mob.getAttackByList().clear();

                    // Stop the AI tasks.
                    if (mob.hasAI()) {
                        mob.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                        mob.getAI().stopAITask();
                    }
                    RandomAnimationTaskManager.getInstance().remove(mob);
                } else if (isNpc(o)) {
                    RandomAnimationTaskManager.getInstance().remove((Npc) o);
                }
            }
        } else {
            for (WorldObject o : objects.values()) {
                if (isAttackable(o)) {
                    // Start HP/MP/CP regeneration task.
                    ((Attackable) o).getStatus().startHpMpRegeneration();
                } else if (isNpc(o)) {
                    RandomAnimationTaskManager.getInstance().add((Npc) o);
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

        objects.remove(object.getObjectId());

        if (isPlayable(object)) {
            if (areNeighborsEmpty() && !Config.GRIDS_ALWAYS_ON) {
                startDeactivation();
            }
        }
    }

    private boolean areNeighborsEmpty() {
        return checkEachSurrounding(Predicate.not(WorldRegion::isActive));
    }

    private boolean checkEachSurrounding(Predicate<WorldRegion> p) {
        for (WorldRegion worldRegion : surroundingRegions) {
            if (!p.test(worldRegion)) {
                return false;
            }
        }
        return true;
    }

    void forEachSurroundingRegion(Consumer<WorldRegion> action) {
        Arrays.stream(surroundingRegions).forEach(action);
    }

    <T extends WorldObject> void forEachObject(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        regionToWorldObjectStream().filter(applyInstanceFilter(clazz, filter)).map(clazz::cast).forEach(action);
    }

    <T extends WorldObject> void forEachObjectInSurrounding(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        filteredParallelSurroundingObjects(clazz, filter).forEach(action);
    }

    private <T extends WorldObject> Stream<T> filteredParallelSurroundingObjects(Class<T> clazz, Predicate<T> filter) {
        return Arrays.stream(surroundingRegions)
                .flatMap(WorldRegion::regionToWorldObjectStream)
                .filter(applyInstanceFilter(clazz, filter)).map(clazz::cast);
    }

    <T extends WorldObject> void forEachObjectInSurroundingLimiting(Class<T> clazz, int limit, Predicate<T> filter, Consumer<T> action) {
        filteredParallelSurroundingObjects(clazz, filter).limit(limit).forEach(action);
    }

    <T extends WorldObject> void forEachOrderedObjectInSurrounding(Class<T> clazz, int maxObjects, Comparator<T> comparator, Predicate<T> filter, Consumer<? super T> action) {
        filteredSurroundingObjects(clazz, filter).sorted(comparator).limit(maxObjects).forEach(action);
    }

    private <T extends WorldObject> Stream<T> filteredSurroundingObjects(Class<T> clazz, Predicate<T> filter) {
        return Arrays.stream(surroundingRegions).flatMap(r -> r.objects.values().stream()).filter(applyInstanceFilter(clazz, filter)).map(clazz::cast);
    }

    <T extends WorldObject> void forAnyObjectInSurrounding(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        filteredSurroundingObjects(clazz, filter).findAny().ifPresent(action);
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
        return  filteredSurroundingObjects(clazz, filter).findAny().orElse(null);
    }

    <T extends WorldObject> T findFirstObjectInSurrounding(Class<T> clazz, Predicate<T> filter, Comparator<T> comparator) {
        return filteredSurroundingObjects(clazz, filter).min(comparator).orElse(null);
    }

    <T extends WorldObject> boolean hasObjectInSurrounding(Class<T> clazz, Predicate<T> filter) {
        return Arrays.stream(surroundingRegions).flatMap(r -> r.objects.values().stream()).anyMatch(applyInstanceFilter(clazz, filter));
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


    private static <T extends WorldObject> Predicate<? super WorldObject> applyInstanceFilter(Class<T> clazz, Predicate<T> filter) {
        return object -> clazz.isInstance(object) && filter.test(clazz.cast(object));
    }

    private Stream<? extends WorldObject> regionToWorldObjectStream() {
        return objects.values().parallelStream();
    }

    @Override
    public String toString() {
        return "[" + regionX + ", " + regionY + "]";
    }

    /**
     * Task of AI notification
     */
    private class NeighborsTask implements Runnable {
        private final boolean isActivating;

        NeighborsTask(boolean isActivating) {
            this.isActivating = isActivating;
        }

        @Override
        public void run() {
            forEachSurroundingRegion(w -> {
                if (isActivating || w.areNeighborsEmpty()) {
                    w.setActive(isActivating);
                }
            });
        }
    }
}
