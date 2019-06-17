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
package org.l2j.gameserver.model;

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.BlockCheckerEngine;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xban1x
 */
public final class ArenaParticipantsHolder {
    private final int _arena;
    private final List<L2PcInstance> _redPlayers;
    private final List<L2PcInstance> _bluePlayers;
    private final BlockCheckerEngine _engine;

    public ArenaParticipantsHolder(int arena) {
        _arena = arena;
        _redPlayers = new ArrayList<>(6);
        _bluePlayers = new ArrayList<>(6);
        _engine = new BlockCheckerEngine(this, _arena);
    }

    public List<L2PcInstance> getRedPlayers() {
        return _redPlayers;
    }

    public List<L2PcInstance> getBluePlayers() {
        return _bluePlayers;
    }

    public List<L2PcInstance> getAllPlayers() {
        final List<L2PcInstance> all = new ArrayList<>(12);
        all.addAll(_redPlayers);
        all.addAll(_bluePlayers);
        return all;
    }

    public void addPlayer(L2PcInstance player, int team) {
        if (team == 0) {
            _redPlayers.add(player);
        } else {
            _bluePlayers.add(player);
        }
    }

    public void removePlayer(L2PcInstance player, int team) {
        if (team == 0) {
            _redPlayers.remove(player);
        } else {
            _bluePlayers.remove(player);
        }
    }

    public int getPlayerTeam(L2PcInstance player) {
        if (_redPlayers.contains(player)) {
            return 0;
        } else if (_bluePlayers.contains(player)) {
            return 1;
        } else {
            return -1;
        }
    }

    public int getRedTeamSize() {
        return _redPlayers.size();
    }

    public int getBlueTeamSize() {
        return _bluePlayers.size();
    }

    public void broadCastPacketToTeam(ServerPacket packet) {
        for (L2PcInstance p : _redPlayers) {
            p.sendPacket(packet);
        }
        for (L2PcInstance p : _bluePlayers) {
            p.sendPacket(packet);
        }
    }

    public void clearPlayers() {
        _redPlayers.clear();
        _bluePlayers.clear();
    }

    public BlockCheckerEngine getEvent() {
        return _engine;
    }

    public void updateEvent() {
        _engine.updatePlayersOnStart(this);
    }

    public void checkAndShuffle() {
        final int redSize = _redPlayers.size();
        final int blueSize = _bluePlayers.size();
        if (redSize > (blueSize + 1)) {
            broadCastPacketToTeam(SystemMessage.getSystemMessage(SystemMessageId.TEAM_MEMBERS_WERE_MODIFIED_BECAUSE_THE_TEAMS_WERE_UNBALANCED));
            final int needed = redSize - (blueSize + 1);
            for (int i = 0; i < (needed + 1); i++) {
                final L2PcInstance plr = _redPlayers.get(i);
                if (plr == null) {
                    continue;
                }
                HandysBlockCheckerManager.getInstance().changePlayerToTeam(plr, _arena, 1);
            }
        } else if (blueSize > (redSize + 1)) {
            broadCastPacketToTeam(SystemMessage.getSystemMessage(SystemMessageId.TEAM_MEMBERS_WERE_MODIFIED_BECAUSE_THE_TEAMS_WERE_UNBALANCED));
            final int needed = blueSize - (redSize + 1);
            for (int i = 0; i < (needed + 1); i++) {
                final L2PcInstance plr = _bluePlayers.get(i);
                if (plr == null) {
                    continue;
                }
                HandysBlockCheckerManager.getInstance().changePlayerToTeam(plr, _arena, 0);
            }
        }
    }
}
