package org.l2j.gameserver.model.actor.tasks.npc.walker;

import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.WalkInfo;
import org.l2j.gameserver.model.actor.Npc;

/**
 * Walker arrive task.
 *
 * @author GKR
 */
public class ArrivedTask implements Runnable {
    private final WalkInfo _walk;
    private final Npc _npc;

    public ArrivedTask(Npc npc, WalkInfo walk) {
        _npc = npc;
        _walk = walk;
    }

    @Override
    public void run() {
        _npc.broadcastInfo();
        _walk.setBlocked(false);
        WalkingManager.getInstance().startMoving(_npc, _walk.getRoute().getName());
    }
}
