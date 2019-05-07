package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class ExAskModifyPartyLooting extends IClientOutgoingPacket {
    private final String _requestor;
    private final PartyDistributionType _partyDistributionType;

    public ExAskModifyPartyLooting(String name, PartyDistributionType partyDistributionType) {
        _requestor = name;
        _partyDistributionType = partyDistributionType;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ASK_MODIFY_PARTY_LOOTING.writeId(packet);

        writeString(_requestor, packet);
        packet.putInt(_partyDistributionType.getId());
    }

    @Override
    protected int size(L2GameClient client) {
        return 11 + _requestor.length() * 2;
    }
}


