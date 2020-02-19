package org.l2j.gameserver.model.actor.tasks.attackable;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.settings.CharacterSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

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

        if ((System.currentTimeMillis() - _attackable.getCommandChannelLastAttack()) > getSettings(CharacterSettings.class).raidLootPrivilegeTime()) {
            _attackable.setCommandChannelTimer(null);
            _attackable.setFirstCommandChannelAttacked(null);
            _attackable.setCommandChannelLastAttack(0);
        } else {
            ThreadPool.schedule(this, 10000); // 10sec
        }
    }

}
