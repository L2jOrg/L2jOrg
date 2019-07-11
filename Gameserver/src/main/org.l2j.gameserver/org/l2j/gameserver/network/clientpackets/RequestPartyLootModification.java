package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JIV
 */
public class RequestPartyLootModification extends ClientPacket {
    private int _partyDistributionTypeId;

    @Override
    public void readImpl() {
        _partyDistributionTypeId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
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
