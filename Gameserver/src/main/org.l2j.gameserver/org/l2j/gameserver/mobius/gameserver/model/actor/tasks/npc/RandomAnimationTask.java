package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.npc;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

/**
 * @author Nik
 */
public class RandomAnimationTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(RandomAnimationTask.class.getName());
    private final L2Npc _npc;
    private boolean _stopTask;

    public RandomAnimationTask(L2Npc npc) {
        _npc = npc;
    }

    @Override
    public void run() {
        if (_stopTask) {
            return;
        }

        try {
            if (!_npc.isInActiveRegion()) {
                return;
            }

            // Cancel further animation timers until intention is changed to ACTIVE again.
            if (_npc.isAttackable() && (_npc.getAI().getIntention() != AI_INTENTION_ACTIVE)) {
                return;
            }

            if (!_npc.isDead() && !_npc.hasBlockActions()) {
                _npc.onRandomAnimation(Rnd.get(2, 3));
            }

            startRandomAnimationTimer();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Execution of RandomAnimationTask has failed.", e);
        }
    }

    /**
     * Create a RandomAnimation Task that will be launched after the calculated delay.
     */
    public void startRandomAnimationTimer() {
        if (!_npc.hasRandomAnimation() || _stopTask) {
            return;
        }

        final int minWait = _npc.isAttackable() ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION;
        final int maxWait = _npc.isAttackable() ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION;

        // Calculate the delay before the next animation
        final int interval = Rnd.get(minWait, maxWait) * 1000;

        // Create a RandomAnimation Task that will be launched after the calculated delay
        ThreadPoolManager.getInstance().schedule(this, interval);
    }

    /**
     * Stops the task from continuing and blocks it from continuing ever again. You need to create new task if you want to start it again.
     */
    public void stopRandomAnimationTimer() {
        _stopTask = true;
    }
}
