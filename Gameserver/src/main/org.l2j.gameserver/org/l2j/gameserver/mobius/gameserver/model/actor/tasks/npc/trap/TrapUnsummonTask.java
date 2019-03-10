package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.npc.trap;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2TrapInstance;

/**
 * Trap unsummon task.
 *
 * @author Zoey76
 */
public class TrapUnsummonTask implements Runnable {
    private final L2TrapInstance _trap;

    public TrapUnsummonTask(L2TrapInstance trap) {
        _trap = trap;
    }

    @Override
    public void run() {
        _trap.unSummon();
    }
}