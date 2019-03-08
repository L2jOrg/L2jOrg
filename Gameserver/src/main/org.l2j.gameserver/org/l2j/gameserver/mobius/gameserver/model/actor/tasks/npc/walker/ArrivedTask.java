package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.npc.walker;

import org.l2j.gameserver.mobius.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.mobius.gameserver.model.WalkInfo;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;

/**
 * Walker arrive task.
 * @author GKR
 */
public class ArrivedTask implements Runnable
{
    private final WalkInfo _walk;
    private final L2Npc _npc;

    public ArrivedTask(L2Npc npc, WalkInfo walk)
    {
        _npc = npc;
        _walk = walk;
    }

    @Override
    public void run()
    {
        _npc.broadcastInfo();
        _walk.setBlocked(false);
        WalkingManager.getInstance().startMoving(_npc, _walk.getRoute().getName());
    }
}
