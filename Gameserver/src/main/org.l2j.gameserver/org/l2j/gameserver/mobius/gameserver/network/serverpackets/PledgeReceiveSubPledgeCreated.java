package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan.SubPledge;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
public class PledgeReceiveSubPledgeCreated extends IClientOutgoingPacket {
    private final SubPledge _subPledge;
    private final L2Clan _clan;

    public PledgeReceiveSubPledgeCreated(SubPledge subPledge, L2Clan clan) {
        _subPledge = subPledge;
        _clan = clan;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PLEDGE_RECEIVE_SUB_PLEDGE_CREATED.writeId(packet);

        packet.putInt(0x01);
        packet.putInt(_subPledge.getId());
        writeString(_subPledge.getName(), packet);
        writeString(getLeaderName(), packet);
    }

    private String getLeaderName() {
        final int LeaderId = _subPledge.getLeaderId();
        if ((_subPledge.getId() == L2Clan.SUBUNIT_ACADEMY) || (LeaderId == 0)) {
            return "";
        } else if (_clan.getClanMember(LeaderId) == null) {
            LOGGER.warn("SubPledgeLeader: " + LeaderId + " is missing from clan: " + _clan.getName() + "[" + _clan.getId() + "]");
            return "";
        } else {
            return _clan.getClanMember(LeaderId).getName();
        }
    }
}
