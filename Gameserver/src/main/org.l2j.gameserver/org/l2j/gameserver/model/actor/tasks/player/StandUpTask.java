package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Task dedicated to put player to stand up.
 *
 * @author UnAfraid
 */
public class StandUpTask implements Runnable {
    private final Player _player;

    public StandUpTask(Player player) {
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
