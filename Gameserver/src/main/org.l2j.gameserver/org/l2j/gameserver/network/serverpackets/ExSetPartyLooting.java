package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class ExSetPartyLooting extends IClientOutgoingPacket {
    private final int _result;
    private final PartyDistributionType _partyDistributionType;

    public ExSetPartyLooting(int result, PartyDistributionType partyDistributionType) {
        _result = result;
        _partyDistributionType = partyDistributionType;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SET_PARTY_LOOTING);

        writeInt(_result);
        writeInt(_partyDistributionType.getId());
    }

}
