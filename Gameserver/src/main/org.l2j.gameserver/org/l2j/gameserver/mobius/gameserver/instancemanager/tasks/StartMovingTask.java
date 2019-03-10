package org.l2j.gameserver.mobius.gameserver.instancemanager.tasks;

import org.l2j.gameserver.mobius.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;

/**
 * Task which starts npc movement.
 *
 * @author xban1x
 */
public final class StartMovingTask implements Runnable {
    final L2Npc _npc;
    final String _routeName;

    public StartMovingTask(L2Npc npc, String routeName) {
        _npc = npc;
        _routeName = routeName;
    }

    @Override
    public void run() {
        if (_npc != null) {
            WalkingManager.getInstance().startMoving(_npc, _routeName);
        }
    }
}
