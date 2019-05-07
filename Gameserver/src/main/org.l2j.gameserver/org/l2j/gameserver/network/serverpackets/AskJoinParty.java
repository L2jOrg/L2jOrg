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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ASK_JOIN_PARTY.writeId(packet);

        writeString(_requestorName, packet);
        packet.putInt(_partyDistributionType.getId());
    }

    @Override
    protected int size(L2GameClient client) {
        return 11 + _requestorName.length() * 2;
    }
}
