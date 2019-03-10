package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * Task dedicated to put player to stand up.
 *
 * @author UnAfraid
 */
public class StandUpTask implements Runnable {
    private final L2PcInstance _player;

    public StandUpTask(L2PcInstance player) {
        _player = player;
    }

    @Override
    public void run() {
        if (_player != null) {
            _player.setIsSitting(false);
            _player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
    }
}
