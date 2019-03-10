package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @author JIV
 */
public class RequestPartyLootModification extends IClientIncomingPacket {
    private int _partyDistributionTypeId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _partyDistributionTypeId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final PartyDistributionType partyDistributionType = PartyDistributionType.findById(_partyDistributionTypeId);
        if (partyDistributionType == null) {
            return;
        }

        final L2Party party = activeChar.getParty();
        if ((party == null) || !party.isLeader(activeChar) || (partyDistributionType == party.getDistributionType())) {
            return;
        }
        party.requestLootChange(partyDistributionType);
    }

}
