package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Mobius
 */
public class HennaDurationTask implements Runnable {
    private final L2PcInstance _player;
    private final int _slot;

    public HennaDurationTask(L2PcInstance player, int slot) {
        _player = player;
        _slot = slot;
    }

    @Override
    public void run() {
        if (_player != null) {
            _player.removeHenna(_slot);
        }
    }
}
