package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.clan.entry.PledgeWaitingInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Sdw
 */
public class ExPledgeDraftListSearch extends IClientOutgoingPacket {
    final List<PledgeWaitingInfo> _pledgeRecruitList;

    public ExPledgeDraftListSearch(List<PledgeWaitingInfo> pledgeRecruitList) {
        _pledgeRecruitList = pledgeRecruitList;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_DRAFT_LIST_SEARCH.writeId(packet);

        packet.putInt(_pledgeRecruitList.size());
        for (PledgeWaitingInfo prl : _pledgeRecruitList) {
            packet.putInt(prl.getPlayerId());
            writeString(prl.getPlayerName(), packet);
            packet.putInt(prl.getKarma());
            packet.putInt(prl.getPlayerClassId());
            packet.putInt(prl.getPlayerLvl());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + _pledgeRecruitList.size() * 55;
    }
}
