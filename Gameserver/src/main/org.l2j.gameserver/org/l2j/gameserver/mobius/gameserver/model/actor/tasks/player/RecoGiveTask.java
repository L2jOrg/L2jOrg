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
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.UserInfo;

/**
 * Task dedicated to increase player's recommendation bonus.
 *
 * @author UnAfraid
 */
public class RecoGiveTask implements Runnable {
    private final L2PcInstance _player;

    public RecoGiveTask(L2PcInstance player) {
        _player = player;
    }

    @Override
    public void run() {
        if (_player != null) {
            // 10 recommendations to give out after 2 hours of being logged in
            // 1 more recommendation to give out every hour after that.
            int recoToGive = 1;
            if (!_player.isRecoTwoHoursGiven()) {
                recoToGive = 10;
                _player.setRecoTwoHoursGiven(true);
            }

            _player.setRecomLeft(_player.getRecomLeft() + recoToGive);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_OBTAINED_S1_RECOMMENDATION_S);
            sm.addInt(recoToGive);
            _player.sendPacket(sm);
            _player.sendPacket(new UserInfo(_player));
        }
    }
}
