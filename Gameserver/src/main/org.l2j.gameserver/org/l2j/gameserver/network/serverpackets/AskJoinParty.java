package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class AskJoinParty extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ASK_JOIN_PARTY);

        writeString(_requestorName);
        writeInt(_partyDistributionType.getId());
    }

}
