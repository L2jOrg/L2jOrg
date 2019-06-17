package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExPledgeDraftListSearch;

/**
 * @author Sdw
 */
public class RequestPledgeDraftListSearch extends ClientPacket {
    private int _levelMin;
    private int _levelMax;
    private int _classId;
    private String _query;
    private int _sortBy;
    private boolean _descending;

    @Override
    public void readImpl() {
        _levelMin = CommonUtil.constrain(readInt(), 0, 107);
        _levelMax = CommonUtil.constrain(readInt(), 0, 107);
        _classId = readInt();
        _query = readString();
        _sortBy = readInt();
        _descending = readInt() == 2;
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();

        if (activeChar == null) {
            return;
        }

        if (_query.isEmpty()) {
            client.sendPacket(new ExPledgeDraftListSearch(ClanEntryManager.getInstance().getSortedWaitingList(_levelMin, _levelMax, _classId, _sortBy, _descending)));
        } else {
            client.sendPacket(new ExPledgeDraftListSearch(ClanEntryManager.getInstance().queryWaitingListByName(_query.toLowerCase())));
        }
    }
}
