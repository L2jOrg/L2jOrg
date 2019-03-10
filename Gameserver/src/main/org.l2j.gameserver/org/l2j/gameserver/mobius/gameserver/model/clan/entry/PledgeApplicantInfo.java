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
package org.l2j.gameserver.mobius.gameserver.model.clan.entry;

import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Sdw
 */
public class PledgeApplicantInfo {
    private final int _playerId;
    private final int _requestClanId;
    private final int _karma;
    private final String _message;
    private String _playerName;
    private int _playerLvl;
    private int _classId;

    public PledgeApplicantInfo(int playerId, String playerName, int playerLevel, int karma, int requestClanId, String message) {
        _playerId = playerId;
        _requestClanId = requestClanId;
        _playerName = playerName;
        _playerLvl = playerLevel;
        _karma = karma;
        _message = message;
    }

    public int getPlayerId() {
        return _playerId;
    }

    public int getRequestClanId() {
        return _requestClanId;
    }

    public String getPlayerName() {
        if (isOnline() && !getPlayerInstance().getName().equalsIgnoreCase(_playerName)) {
            _playerName = getPlayerInstance().getName();
        }
        return _playerName;
    }

    public int getPlayerLvl() {
        if (isOnline() && (getPlayerInstance().getLevel() != _playerLvl)) {
            _playerLvl = getPlayerInstance().getLevel();
        }
        return _playerLvl;
    }

    public int getClassId() {
        if (isOnline() && (getPlayerInstance().getBaseClass() != _classId)) {
            _classId = getPlayerInstance().getClassId().getId();
        }
        return _classId;
    }

    public String getMessage() {
        return _message;
    }

    public int getKarma() {
        return _karma;
    }

    public L2PcInstance getPlayerInstance() {
        return L2World.getInstance().getPlayer(_playerId);
    }

    public boolean isOnline() {
        return (getPlayerInstance() != null) && (getPlayerInstance().isOnlineInt() > 0);
    }
}