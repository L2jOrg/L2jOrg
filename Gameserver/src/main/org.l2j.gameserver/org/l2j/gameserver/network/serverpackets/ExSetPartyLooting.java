package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author JIV
 */
public class ExSetPartyLooting extends ServerPacket {
    private final int _result;
    private final PartyDistributionType _partyDistributionType;

    public ExSetPartyLooting(int result, PartyDistributionType partyDistributionType) {
        _result = result;
        _partyDistributionType = partyDistributionType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SET_PARTY_LOOTING);

        writeInt(_result);
        writeInt(_partyDistributionType.getId());
    }

}
