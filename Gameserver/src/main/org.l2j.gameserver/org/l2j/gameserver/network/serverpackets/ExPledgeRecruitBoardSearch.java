package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardSearch extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_SEARCH);

        writeInt(_currentPage);
        writeInt(_totalNumberOfPage);
        writeInt(_clanOnCurrentPage);

        for (int i = _startIndex; i < _endIndex; i++) {
            writeInt(_clanList.get(i).getClanId());
            writeInt(_clanList.get(i).getClan().getAllyId());
        }
        for (int i = _startIndex; i < _endIndex; i++) {
            final L2Clan clan = _clanList.get(i).getClan();
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
