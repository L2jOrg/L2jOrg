package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.attackable;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;

/**
 * @author xban1x
 */
public final class CommandChannelTimer implements Runnable {
    private final L2Attackable _attackable;

    public CommandChannelTimer(L2Attackable attackable) {
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
