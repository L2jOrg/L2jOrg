/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.model.Party;
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
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final PartyDistributionType partyDistributionType = PartyDistributionType.findById(_partyDistributionTypeId);
        if (partyDistributionType == null) {
            return;
        }

        final Party party = activeChar.getParty();
        if ((party == null) || !party.isLeader(activeChar) || (partyDistributionType == party.getDistributionType())) {
            return;
        }
        party.requestLootChange(partyDistributionType);
    }

}
