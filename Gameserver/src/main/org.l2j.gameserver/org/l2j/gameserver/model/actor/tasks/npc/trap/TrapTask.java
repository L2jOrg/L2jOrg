package org.l2j.gameserver.model.actor.tasks.npc.trap;

import org.l2j.gameserver.model.actor.instance.L2TrapInstance;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Trap task.
 *
 * @author Zoey76
 */
public class TrapTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrapTask.class);
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
            LOGGER.error(TrapTask.class.getSimpleName() + ": " + e.getMessage());
            _trap.unSummon();
        }
    }
}
