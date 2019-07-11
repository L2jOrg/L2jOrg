package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.actor.Attackable;

/**
 * @author xban1x
 */
public final class CommandChannelTimer implements Runnable {
    private final Attackable _attackable;

    public CommandChannelTimer(Attackable attackable) {
        _attackable = attackable;
    }

    @Override
    public void run() {
        if (_attackable == null) {
            return;
        }

        if ((System.currentTimeMillis() - _attackable.getCommandChannelLastAttack()) > Config.LOOT_RAIDS_PRIVILEGE_INTERVAL) {
            _attackable.setCommandChannelTimer(null);
            _attackable.setFirstCommandChannelAttacked(null);
            _attackable.setCommandChannelLastAttack(0);
        } else {
            ThreadPoolManager.getInstance().schedule(this, 10000); // 10sec
        }
    }

}
