package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.npc.trap;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2TrapInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SocialAction;

import java.util.logging.Logger;

/**
 * Trap task.
 *
 * @author Zoey76
 */
public class TrapTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(TrapTask.class.getName());
    private static final int TICK = 1000; // 1s
    private final L2TrapInstance _trap;

    public TrapTask(L2TrapInstance trap) {
        _trap = trap;
    }

    @Override
    public void run() {
        try {
            if (!_trap.isTriggered()) {
                if (_trap.hasLifeTime()) {
                    _trap.setRemainingTime(_trap.getRemainingTime() - TICK);
                    if (_trap.getRemainingTime() < (_trap.getLifeTime() - 15000)) {
                        _trap.broadcastPacket(new SocialAction(_trap.getObjectId(), 2));
                    }
                    if (_trap.getRemainingTime() <= 0) {
                        _trap.triggerTrap(_trap);
                        return;
                    }
                }

                if (!_trap.getSkill().getTargetsAffected(_trap, _trap).isEmpty()) {
                    _trap.triggerTrap(_trap);
                }
            }
        } catch (Exception e) {
            LOGGER.severe(TrapTask.class.getSimpleName() + ": " + e.getMessage());
            _trap.unSummon();
        }
    }
}
