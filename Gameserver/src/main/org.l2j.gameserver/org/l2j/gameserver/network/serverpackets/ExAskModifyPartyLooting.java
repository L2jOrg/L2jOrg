package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JIV
 */
public class ExAskModifyPartyLooting extends ServerPacket {
    private final String _requestor;
    private final PartyDistributionType _partyDistributionType;

    public ExAskModifyPartyLooting(String name, PartyDistributionType partyDistributionType) {
        _requestor = name;
        _partyDistributionType = partyDistributionType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_ASK_MODIFY_PARTY_LOOTING);

        writeString(_requestor);
        writeInt(_partyDistributionType.getId());
    }

}


