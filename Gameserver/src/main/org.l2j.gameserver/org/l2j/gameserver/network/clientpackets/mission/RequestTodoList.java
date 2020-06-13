/*
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
package org.l2j.gameserver.network.clientpackets.mission;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.mission.ExOneDayReceiveRewardList;

/**
 * @author UnAfraid
 */
public class RequestTodoList extends ClientPacket {
    private int _tab;
    @SuppressWarnings("unused")
    private boolean _showAllLevels;

    @Override
    public void readImpl() {
        _tab = readByte(); // Daily Reward = 9, Event = 1, Instance Zone = 2
        _showAllLevels = readByte() == 1; // Disabled = 0, Enabled = 1
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        switch (_tab) {
            // case 1:
            // {
            // player.sendPacket(new ExTodoListInzone());
            // break;
            // }
            // case 2:
            // {
            // player.sendPacket(new ExTodoListInzone());
            // break;
            // }
            case 9: // Daily Rewards
            {
                // Initial EW request should be false
                player.sendPacket(new ExOneDayReceiveRewardList(player, true));
                break;
            }
        }
    }
}
