package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;


public final class WorldRegion {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldRegion.class);
    private final int _regionX;
    private final int _regionY;
    /**
     * Map containing visible objects in this world region.
     */
    private volatile Map<Integer, WorldObject> objects = new ConcurrentHashMap<>();
    /**
     * Map containing nearby regions forming this world region's effective area.
     */
    private WorldRegion[] surroundingRegions;
    private boolean active;
    private ScheduledFuture<?> _neighborsTask = null;

    public WorldRegion(int regionX, int regionY) {
        _regionX = regionX;
        _regionY = regionY;

        // Default a newly initialized region to inactive, unless always on is specified.
        active = Config.GRIDS_ALWAYS_ON;
    }

    private void switchAI(boolean isOn) {
        if (objects.isEmpty()) {
            return;
        }

        int c = 0;
        if (!isOn) {
            for (WorldObject o : objects.values()) {
                if (o.isAttackable()) {
                    c++;
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
                } else if (o instanceof Vehicle) {
                    c++;
                }
            }
            LOGGER.debug(c + " mobs were turned off");
        } else {
            for (WorldObject o : objects.values()) {
                if (o.isAttackable()) {
                    c++;
                    // Start HP/MP/CP regeneration task.
                    ((Attackable) o).getStatus().startHpMpRegeneration();
                } else if (o instanceof Npc) {
                    ((Npc) o).startRandomAnimationTask();
                }
            }
            LOGGER.debug(c + " mobs were turned on");
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * this function turns this region's AI and geodata on or off
     *
     * @param value
     */
    public void setActive(boolean value) {
        if (active == value) {
            return;
        }

        active = value;

        // Turn the AI on or off to match the region's activation.
        switchAI(value);

        LOGGER.debug((value ? "Starting" : "Stopping") + " Grid " + this);
    }

    public boolean areNeighborsEmpty() {
        return checkEachSurroundingRegion(w -> !(w.isActive() && w.getObjects().values().stream().anyMatch(WorldObject::isPlayable)));
    }

    /**
     * Immediately sets self as active and starts a timer to set neighbors as active this timer is to avoid turning on neighbors in the case when a person just teleported into a region and then teleported out immediately...there is no reason to activate all the neighbors in that case.
     */
    private void startActivation() {
        // First set self to active and do self-tasks...
        setActive(true);

        // If the timer to deactivate neighbors is running, cancel it.
        synchronized (this) {
            if (_neighborsTask != null) {
                _neighborsTask.cancel(true);
                _neighborsTask = null;
            }

            // Then, set a timer to activate the neighbors.
            _neighborsTask = ThreadPoolManager.schedule(new NeighborsTask(true), 1000 * Config.GRID_NEIGHBOR_TURNON_TIME);
        }
    }

    /**
     * starts a timer to set neighbors (including self) as inactive this timer is to avoid turning off neighbors in the case when a person just moved out of a region that he may very soon return to. There is no reason to turn self & neighbors off in that case.
     */
    private void startDeactivation() {
        // If the timer to activate neighbors is running, cancel it.
        synchronized (this) {
            if (_neighborsTask != null) {
                _neighborsTask.cancel(true);
                _neighborsTask = null;
            }

            // Start a timer to "suggest" a deactivate to self and neighbors.
            // Suggest means: first check if a neighbor has L2PcInstances in it. If not, deactivate.
            _neighborsTask = ThreadPoolManager.schedule(new NeighborsTask(false), 1000 * Config.GRID_NEIGHBOR_TURNOFF_TIME);
        }
    }

    /**
     * Add the WorldObject in the L2ObjectHashSet(WorldObject) objects containing WorldObject visible in this WorldRegion <BR>
     * If WorldObject is a Player, Add the Player in the L2ObjectHashSet(Player) _allPlayable containing Player of all player in game in this WorldRegion <BR>
     *
     * @param object
     */
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
     * Remove the WorldObject from the L2ObjectHashSet(WorldObject) objects in this WorldRegion. If WorldObject is a Player, remove it from the L2ObjectHashSet(Player) _allPlayable of this WorldRegion <BR>
     *
     * @param object
     */
    public void removeVisibleObject(WorldObject object) {
        if (object == null) {
            return;
        }

        if (objects.isEmpty()) {
            return;
        }
        objects.remove(object.getObjectId());

        if (isPlayable(object)) {
            if (areNeighborsEmpty() && !Config.GRIDS_ALWAYS_ON) {
                startDeactivation();
            }
        }
    }

    public Map<Integer, WorldObject> getObjects() {
        return objects;
    }

    public boolean checkEachSurroundingRegion(Predicate<WorldRegion> p) {
        for (WorldRegion worldRegion : surroundingRegions) {
            if (!p.test(worldRegion)) {
                return false;
            }
        }
        return true;
    }

    <T extends WorldObject> void forEachObject(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        getObjects().values().parallelStream().forEach(object -> {
            T acceptedObject;
            if(clazz.isInstance(object) && filter.test(acceptedObject = clazz.cast(object))) {
                action.accept(acceptedObject);
            }
        });
    }

    <T extends WorldObject> void forEachObjectInSurround(Class<T> clazz, Consumer<T> action, Predicate<T> filter) {
        Arrays.stream(surroundingRegions).flatMap(r -> r.getObjects().values().parallelStream()).forEach(object -> {
            T acceptedObject;
            if(clazz.isInstance(object) && filter.test(acceptedObject = clazz.cast(object)) ) {
                action.accept(acceptedObject);
            }
        });
    }

    public WorldRegion[] getSurroundingRegions() {
        return surroundingRegions;
    }

    public void setSurroundingRegions(WorldRegion[] regions) {
        surroundingRegions = regions;
    }

    public boolean isSurroundingRegion(WorldRegion region) {
        return (region != null) && (_regionX >= (region.getRegionX() - 1)) && (_regionX <= (region.getRegionX() + 1)) && (_regionY >= (region.getRegionY() - 1)) && (_regionY <= (region.getRegionY() + 1));
    }

    public int getRegionX() {
        return _regionX;
    }

    public int getRegionY() {
        return _regionY;
    }

    @Override
    public String toString() {
        return "(" + _regionX + ", " + _regionY + ")";
    }

    /**
     * Task of AI notification
     */
    public class NeighborsTask implements Runnable {
        private final boolean _isActivating;

        public NeighborsTask(boolean isActivating) {
            _isActivating = isActivating;
        }

        @Override
        public void run() {
            checkEachSurroundingRegion(w ->
            {
                if (_isActivating || w.areNeighborsEmpty()) {
                    w.setActive(_isActivating);
                }
                return true;
            });
        }
    }
}
