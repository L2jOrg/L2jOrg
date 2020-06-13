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

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardSearch extends ServerPacket {
    static final int CLAN_PER_PAGE = 12;
    final List<PledgeRecruitInfo> _clanList;
    private final int _currentPage;
    private final int _totalNumberOfPage;
    private final int _clanOnCurrentPage;
    private final int _startIndex;
    private final int _endIndex;

    public ExPledgeRecruitBoardSearch(List<PledgeRecruitInfo> clanList, int currentPage) {
        _clanList = clanList;
        _currentPage = currentPage;
        _totalNumberOfPage = (int) Math.ceil((double) _clanList.size() / CLAN_PER_PAGE);
        _startIndex = (_currentPage - 1) * CLAN_PER_PAGE;
        _endIndex = (_startIndex + CLAN_PER_PAGE) > _clanList.size() ? _clanList.size() : _startIndex + CLAN_PER_PAGE;
        _clanOnCurrentPage = _endIndex - _startIndex;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_RECRUIT_BOARD_SEARCH);

        writeInt(_currentPage);
        writeInt(_totalNumberOfPage);
        writeInt(_clanOnCurrentPage);

        for (int i = _startIndex; i < _endIndex; i++) {
            writeInt(_clanList.get(i).getClanId());
            writeInt(_clanList.get(i).getClan().getAllyId());
        }
        for (int i = _startIndex; i < _endIndex; i++) {
            final Clan clan = _clanList.get(i).getClan();
            writeInt(clan.getCrestId());
            writeInt(clan.getAllyCrestId());
            writeString(clan.getName());
            writeString(clan.getLeaderName());
            writeInt(clan.getLevel());
            writeInt(clan.getMembersCount());
            writeInt(_clanList.get(i).getKarma());
            writeString(_clanList.get(i).getInformation());
            writeInt(_clanList.get(i).getApplicationType()); // Helios
            writeInt(_clanList.get(i).getRecruitType()); // Helios
        }
    }

}
