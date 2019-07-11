package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.l2j.gameserver.model.actor.instance.Trap;

/**
 * Trap unsummon task.
 *
 * @author Zoey76
 */
public class TrapUnsummonTask implements Runnable {
    private final Trap _trap;

    public TrapUnsummonTask(Trap trap) {
        _trap = trap;
    }

    @Override
    public void run() {
        _trap.unSummon();
    }
}