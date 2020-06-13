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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * ExVoteSystemInfo packet implementation.
 *
 * @author Gnacik
 */
public class ExVoteSystemInfo extends ServerPacket {
    private final int _recomLeft;
    private final int _recomHave;
    private final int _bonusTime;
    private final int _bonusVal;
    private final int _bonusType;

    public ExVoteSystemInfo(Player player) {
        _recomLeft = player.getRecomLeft();
        _recomHave = player.getRecomHave();
        _bonusTime = 0;
        _bonusVal = 0;
        _bonusType = 0;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_VOTE_SYSTEM_INFO);

        writeInt(_recomLeft);
        writeInt(_recomHave);
        writeInt(_bonusTime);
        writeInt(_bonusVal);
        writeInt(_bonusType);
    }

}
