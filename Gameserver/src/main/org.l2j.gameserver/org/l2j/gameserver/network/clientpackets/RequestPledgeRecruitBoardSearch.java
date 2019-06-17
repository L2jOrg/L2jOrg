package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitBoardSearch;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitBoardSearch extends ClientPacket {
    private int _clanLevel;
    private int _karma;
    private int _type;
    private String _query;
    private int _sort;
    private boolean _descending;
    private int _page;
    @SuppressWarnings("unused")
    private int _applicationType;

    @Override
    public void readImpl() {
        _clanLevel = readInt();
        _karma = readInt();
        _type = readInt();
        _query = readString();
        _sort = readInt();
        _descending = readInt() == 2;
        _page = readInt();
        _applicationType = readInt(); // Helios2
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();

        if (activeChar == null) {
            return;
        }

        if (_query.isEmpty()) {
            if ((_karma < 0) && (_clanLevel < 0)) {
                activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getUnSortedClanList(), _page));
            } else {
                activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanList(_clanLevel, _karma, _sort, _descending), _page));
            }
        } else {
            activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanListByName(_query.toLowerCase(), _type), _page));
        }
    }
}
