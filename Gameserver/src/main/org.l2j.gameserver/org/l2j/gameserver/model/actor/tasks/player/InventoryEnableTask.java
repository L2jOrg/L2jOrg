package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Task dedicated to enable player's inventory.
 *
 * @author UnAfraid
 */
public class InventoryEnableTask implements Runnable {
    private final Player _player;

    public InventoryEnableTask(Player player) {
        _player = player;
    }

    @Override
    public void run() {
        if (_player != null) {
            _player.setInventoryBlockingStatus(false);
        }
    }
}
