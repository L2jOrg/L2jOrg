package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.SubPledgeData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author -Wooden-
 */
public class PledgeReceiveSubPledgeCreated extends ServerPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(PledgeReceiveSubPledgeCreated.class);

    private final SubPledgeData _subPledge;
    private final Clan _clan;

    public PledgeReceiveSubPledgeCreated(SubPledgeData subPledge, Clan clan) {
        _subPledge = subPledge;
        _clan = clan;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PLEDGE_RECEIVE_SUB_PLEDGE_CREATED);

        writeInt(0x01);
        writeInt(_subPledge.getId());
        writeString(_subPledge.getName());
        writeString(getLeaderName());
    }


    private String getLeaderName() {
        final int LeaderId = _subPledge.getLeaderId();
        if ((_subPledge.getId() == Clan.SUBUNIT_ACADEMY) || (LeaderId == 0)) {
            return "";
        } else if (_clan.getClanMember(LeaderId) == null) {
            LOGGER.warn("SubPledgeLeader: " + LeaderId + " is missing from clan: " + _clan.getName() + "[" + _clan.getId() + "]");
            return "";
        } else {
            return _clan.getClanMember(LeaderId).getName();
        }
    }
}
