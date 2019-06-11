package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class AskJoinParty extends IClientOutgoingPacket {
    private final String _requestorName;
    private final PartyDistributionType _partyDistributionType;

    /**
     * @param requestorName
     * @param partyDistributionType
     */
    public AskJoinParty(String requestorName, PartyDistributionType partyDistributionType) {
        _requestorName = requestorName;
        _partyDistributionType = partyDistributionType;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.ASK_JOIN_PARTY);

        writeString(_requestorName);
        writeInt(_partyDistributionType.getId());
    }

}
