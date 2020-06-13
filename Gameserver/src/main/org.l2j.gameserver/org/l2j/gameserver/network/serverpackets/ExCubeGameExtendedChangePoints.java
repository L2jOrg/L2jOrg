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
 * @author mrTJO
 */
public class ExCubeGameExtendedChangePoints extends ServerPacket {
    int _timeLeft;
    int _bluePoints;
    int _redPoints;
    boolean _isRedTeam;
    Player _player;
    int _playerPoints;

    /**
     * Update a Secret Point Counter (used by client when receive ExCubeGameEnd)
     *
     * @param timeLeft     Time Left before Minigame's End
     * @param bluePoints   Current Blue Team Points
     * @param redPoints    Current Blue Team points
     * @param isRedTeam    Is Player from Red Team?
     * @param player       Player Instance
     * @param playerPoints Current Player Points
     */
    public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, Player player, int playerPoints) {
        _timeLeft = timeLeft;
        _bluePoints = bluePoints;
        _redPoints = redPoints;
        _isRedTeam = isRedTeam;
        _player = player;
        _playerPoints = playerPoints;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BLOCK_UPSET_STATE);

        writeInt(0x00);

        writeInt(_timeLeft);
        writeInt(_bluePoints);
        writeInt(_redPoints);

        writeInt(_isRedTeam ? 0x01 : 0x00);
        writeInt(_player.getObjectId());
        writeInt(_playerPoints);
    }

}
