/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.SayuneRequest;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune.ExNotifyFlyMoveStart;

import java.util.Objects;

/**
 * @author UnAfraid
 */
public class FlyMoveStartTask implements Runnable {
    private final L2PcInstance _player;
    private final L2ZoneType _zone;

    public FlyMoveStartTask(L2ZoneType zone, L2PcInstance player) {
        Objects.requireNonNull(zone);
        Objects.requireNonNull(player);
        _player = player;
        _zone = zone;
    }

    @Override
    public void run() {
        if (!_zone.isCharacterInZone(_player)) {
            return;
        }

        if (!_player.hasRequest(SayuneRequest.class)) {
            _player.sendPacket(ExNotifyFlyMoveStart.STATIC_PACKET);
            ThreadPoolManager.getInstance().schedule(this, 1000);
        }
    }
}