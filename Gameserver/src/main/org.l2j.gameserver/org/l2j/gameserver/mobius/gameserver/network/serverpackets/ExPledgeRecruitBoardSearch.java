package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_SEARCH.writeId(packet);

        packet.putInt(_currentPage);
        packet.putInt(_totalNumberOfPage);
        packet.putInt(_clanOnCurrentPage);

        for (int i = _startIndex; i < _endIndex; i++) {
            packet.putInt(_clanList.get(i).getClanId());
            packet.putInt(_clanList.get(i).getClan().getAllyId());
        }
        for (int i = _startIndex; i < _endIndex; i++) {
            final L2Clan clan = _clanList.get(i).getClan();
            packet.putInt(clan.getCrestId());
            packet.putInt(clan.getAllyCrestId());
            writeString(clan.getName(), packet);
            writeString(clan.getLeaderName(), packet);
            packet.putInt(clan.getLevel());
            packet.putInt(clan.getMembersCount());
            packet.putInt(_clanList.get(i).getKarma());
            writeString(_clanList.get(i).getInformation(), packet);
            packet.putInt(_clanList.get(i).getApplicationType()); // Helios
            packet.putInt(_clanList.get(i).getRecruitType()); // Helios
        }
    }
}
