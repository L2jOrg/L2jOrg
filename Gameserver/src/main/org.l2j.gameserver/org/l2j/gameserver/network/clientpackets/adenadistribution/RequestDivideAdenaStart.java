package org.l2j.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
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
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final L2Party party = player.getParty();

        if (party == null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY);
            return;
        }

        final L2CommandChannel commandChannel = party.getCommandChannel();

        if ((commandChannel != null) && !commandChannel.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_AN_ALLIANCE_LEADER_OR_PARTY_LEADER);
            return;
        } else if (!party.isLeader(player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
            return;
        }

        final List<L2PcInstance> targets = commandChannel != null ? commandChannel.getMembers() : party.getMembers();

        if (player.getAdena() < targets.size()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_2);
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
