/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaStart;

import java.util.List;

/**
 * @author Sdw
 */
public class RequestDivideAdenaStart extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final Party party = player.getParty();

        if (party == null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY);
            return;
        }

        final CommandChannel commandChannel = party.getCommandChannel();

        if ((commandChannel != null) && !commandChannel.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_AN_ALLIANCE_LEADER_OR_PARTY_LEADER);
            return;
        } else if (!party.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
            return;
        }

        final List<Player> targets = commandChannel != null ? commandChannel.getMembers() : party.getMembers();

        if (player.getAdena() < targets.size()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (targets.stream().anyMatch(t -> t.hasRequest(AdenaDistributionRequest.class))) {
            // Handle that case ?
            return;
        }

        final int adenaObjectId = player.getInventory().getAdenaInstance().getObjectId();

        targets.forEach(t ->
        {
            t.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_STARTED);
            t.addRequest(new AdenaDistributionRequest(t, player, targets, adenaObjectId, player.getAdena()));
        });

        player.sendPacket(ExDivideAdenaStart.STATIC_PACKET);
    }
}
