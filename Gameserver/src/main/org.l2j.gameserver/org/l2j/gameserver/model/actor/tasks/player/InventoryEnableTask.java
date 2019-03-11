package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * Task dedicated to enable player's inventory.
 *
 * @author UnAfraid
 */
public class InventoryEnableTask implements Runnable {
    private final L2PcInstance _player;

    public InventoryEnableTask(L2PcInstance player) {
        _player = player;
    }

    @Override
    public void run() {
        if (_player != null) {
            _player.setInventoryBlockingStatus(false);
        }
    }
}
