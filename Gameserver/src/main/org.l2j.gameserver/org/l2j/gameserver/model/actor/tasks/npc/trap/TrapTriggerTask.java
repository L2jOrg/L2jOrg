package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.actor.instance.Trap;

/**
 * Trap trigger task.
 *
 * @author Zoey76
 */
public class TrapTriggerTask implements Runnable {
    private final Trap _trap;

    public TrapTriggerTask(Trap trap) {
        _trap = trap;
    }

    @Override
    public void run() {
        try {
            _trap.doCast(_trap.getSkill());
            ThreadPoolManager.getInstance().schedule(new TrapUnsummonTask(_trap), _trap.getSkill().getHitTime() + 300);
        } catch (Exception e) {
            _trap.unSummon();
        }
    }
}
