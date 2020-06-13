/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * Task dedicated to reward player with fame while standing on siege zone.
 *
 * @author UnAfraid
 */
public class FameTask implements Runnable {
    private final Player _player;
    private final int _value;

    public FameTask(Player player, int value) {
        _player = player;
        _value = value;
    }

    @Override
    public void run() {
        if ((_player == null) || (_player.isDead() && !Config.FAME_FOR_DEAD_PLAYERS)) {
            return;
        }
        if (((_player.getClient() == null) || _player.getClient().isDetached()) && !Config.OFFLINE_FAME) {
            return;
        }
        _player.setFame(_player.getFame() + _value);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_FAME);
        sm.addInt(_value);
        _player.sendPacket(sm);
        _player.sendPacket(new UserInfo(_player));
    }
}
