package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.holders.WarpedSpaceHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.util.GameUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sdw
 */
public class WarpedSpaceManager {
    private volatile ConcurrentHashMap<L2Character, WarpedSpaceHolder> _warpedSpace = null;

    private WarpedSpaceManager() {
    }

    public void addWarpedSpace(L2Character creature, int radius) {
        if (_warpedSpace == null) {
            synchronized (this) {
                if (_warpedSpace == null) {
                    _warpedSpace = new ConcurrentHashMap<>();
                }
            }
        }
        _warpedSpace.put(creature, new WarpedSpaceHolder(creature, radius));
    }

    public void removeWarpedSpace(L2Character creature) {
        _warpedSpace.remove(creature);
    }

    public boolean checkForWarpedSpace(Location origin, Location destination, Instance instance) {
        if (_warpedSpace != null) {
            for (WarpedSpaceHolder holder : _warpedSpace.values()) {
                final L2Character creature = holder.getCreature();
                if (creature.getInstanceWorld() != instance) {
                    continue;
                }
                final int radius = creature.getTemplate().getCollisionRadius();
                final boolean originInRange = GameUtils.calculateDistance(creature, origin, false, false) <= (holder.getRange() + radius);
                final boolean destinationInRange = GameUtils.calculateDistance(creature, destination, false, false) <= (holder.getRange() + radius);
                return destinationInRange != originInRange;
            }
        }
        return false;
    }

    public static WarpedSpaceManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final WarpedSpaceManager INSTANCE = new WarpedSpaceManager();
    }
}
