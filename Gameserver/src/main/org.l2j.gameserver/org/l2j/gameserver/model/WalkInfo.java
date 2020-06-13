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
package org.l2j.gameserver.model;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveRouteFinished;

import java.util.concurrent.ScheduledFuture;

/**
 * Holds info about current walk progress.
 *
 * @author GKR, UnAfraid
 */
public class WalkInfo {
    private final String _routeName;
    private ScheduledFuture<?> _walkCheckTask;
    private boolean _blocked = false;
    private boolean _suspended = false;
    private boolean _stoppedByAttack = false;
    private int _currentNode = 0;
    private boolean _forward = true; // Determines first --> last or first <-- last direction
    private long _lastActionTime; // Debug field

    public WalkInfo(String routeName) {
        _routeName = routeName;
    }

    /**
     * @return name of route of this WalkInfo.
     */
    public WalkRoute getRoute() {
        return WalkingManager.getInstance().getRoute(_routeName);
    }

    /**
     * @return current node of this WalkInfo.
     */
    public NpcWalkerNode getCurrentNode() {
        return getRoute().getNodeList().get(Math.min(Math.max(0, _currentNode), getRoute().getNodeList().size() - 1));
    }

    /**
     * Calculate next node for this WalkInfo and send debug message from given npc
     *
     * @param npc NPC to debug message to be sent from
     */
    public synchronized void calculateNextNode(Npc npc) {
        // Check this first, within the bounds of random moving, we have no conception of "first" or "last" node
        if (getRoute().getRepeatType() == WalkingManager.REPEAT_RANDOM) {
            int newNode = _currentNode;

            while (newNode == _currentNode) {
                newNode = Rnd.get(getRoute().getNodesCount());
            }

            _currentNode = newNode;
        } else {
            if (_forward) {
                _currentNode++;
            } else {
                _currentNode--;
            }

            if (_currentNode == getRoute().getNodesCount()) // Last node arrived
            {
                // Notify quest
                EventDispatcher.getInstance().notifyEventAsync(new OnNpcMoveRouteFinished(npc), npc);

                if (!getRoute().repeatWalk()) {
                    WalkingManager.getInstance().cancelMoving(npc);
                    return;
                }

                switch (getRoute().getRepeatType()) {
                    case WalkingManager.REPEAT_GO_BACK: {
                        _forward = false;
                        _currentNode -= 2;
                        break;
                    }
                    case WalkingManager.REPEAT_GO_FIRST: {
                        _currentNode = 0;
                        break;
                    }
                    case WalkingManager.REPEAT_TELE_FIRST: {
                        npc.teleToLocation(npc.getSpawn().getLocation());
                        _currentNode = 0;
                        break;
                    }
                }
            } else if (_currentNode == WalkingManager.NO_REPEAT) // First node arrived, when direction is first <-- last
            {
                _currentNode = 1;
                _forward = true;
            }
        }
    }

    /**
     * @return {@code true} if walking task is blocked, {@code false} otherwise,
     */
    public boolean isBlocked() {
        return _blocked;
    }

    /**
     * @param val
     */
    public void setBlocked(boolean val) {
        _blocked = val;
    }

    /**
     * @return {@code true} if walking task is suspended, {@code false} otherwise,
     */
    public boolean isSuspended() {
        return _suspended;
    }

    /**
     * @param val
     */
    public void setSuspended(boolean val) {
        _suspended = val;
    }

    /**
     * @return {@code true} if walking task shall be stopped by attack, {@code false} otherwise,
     */
    public boolean isStoppedByAttack() {
        return _stoppedByAttack;
    }

    /**
     * @param val
     */
    public void setStoppedByAttack(boolean val) {
        _stoppedByAttack = val;
    }

    /**
     * @return the id of the current node in this walking task.
     */
    public int getCurrentNodeId() {
        return _currentNode;
    }

    /**
     * @return {@code long} last action time used only for debugging.
     */
    public long getLastAction() {
        return _lastActionTime;
    }

    /**
     * @param val
     */
    public void setLastAction(long val) {
        _lastActionTime = val;
    }

    /**
     * @return walking check task.
     */
    public ScheduledFuture<?> getWalkCheckTask() {
        return _walkCheckTask;
    }

    /**
     * @param val walking check task.
     */
    public void setWalkCheckTask(ScheduledFuture<?> val) {
        _walkCheckTask = val;
    }

    @Override
    public String toString() {
        return "WalkInfo [_routeName=" + _routeName + ", _walkCheckTask=" + _walkCheckTask + ", _blocked=" + _blocked + ", _suspended=" + _suspended + ", _stoppedByAttack=" + _stoppedByAttack + ", _currentNode=" + _currentNode + ", _forward=" + _forward + ", _lastActionTime=" + _lastActionTime + "]";
    }
}
