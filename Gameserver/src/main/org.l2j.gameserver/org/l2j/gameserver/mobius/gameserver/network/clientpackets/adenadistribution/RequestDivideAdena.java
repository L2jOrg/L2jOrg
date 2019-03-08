package org.l2j.gameserver.mobius.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.mobius.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaDone;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

/**
 * @author Sdw
 */
public class RequestDivideAdena extends IClientIncomingPacket
{
    private int _adenaObjId;
    private long _adenaCount;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _adenaObjId = packet.getInt();
        _adenaCount = packet.getLong();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player == null)
        {
            return;
        }

        final AdenaDistributionRequest request = player.getRequest(AdenaDistributionRequest.class);

        if (request == null)
        {
            return;
        }
        else if (request.getDistributor() != player)
        {
            cancelDistribution(request);
            return;
        }
        else if (request.getAdenaObjectId() != _adenaObjId)
        {
            cancelDistribution(request);
            return;
        }

        final L2Party party = player.getParty();

        if (party == null)
        {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY);
            cancelDistribution(request);
            return;
        }

        final L2CommandChannel commandChannel = party.getCommandChannel();

        if ((commandChannel != null) && !commandChannel.isLeader(player))
        {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_AN_ALLIANCE_LEADER_OR_PARTY_LEADER);
            cancelDistribution(request);
            return;
        }
        else if (!party.isLeader(player))
        {
            player.sendPacket(SystemMessageId.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER);
            cancelDistribution(request);
            return;
        }

        final List<L2PcInstance> targets = commandChannel != null ? commandChannel.getMembers() : party.getMembers();

        if (player.getAdena() < targets.size())
        {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_2);
            cancelDistribution(request);
            return;
        }

        if (player.getAdena() < request.getAdenaCount())
        {
            player.sendPacket(SystemMessageId.THE_ADENA_IN_POSSESSION_HAS_BEEN_DECREASED_ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
            cancelDistribution(request);
            return;
        }
        else if (targets.size() < request.getPlayers().size())
        {
            player.sendPacket(SystemMessageId.THE_DISTRIBUTION_PARTICIPANTS_HAVE_CHANGED_ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
            cancelDistribution(request);
            return;
        }
        else if (player.getAdena() < _adenaCount)
        {
            player.sendPacket(SystemMessageId.DISTRIBUTION_CANNOT_PROCEED_AS_THERE_IS_INSUFFICIENT_ADENA_FOR_DISTRIBUTION);
            cancelDistribution(request);
            return;
        }

        final long memberAdenaGet = (long) Math.floor(_adenaCount / (float) targets.size());
        if (player.reduceAdena("Adena Distribution", memberAdenaGet * targets.size(), player, false))
        {
            for (L2PcInstance target : targets)
            {
                if ((target == null))
                {
                    // TODO : handle that case here + regive adena OR filter with Objects::nonNull on memberCount ?
                    // those sys msg exists and bother me ADENA_WAS_NOT_DISTRIBUTED_TO_S1 / YOU_DID_NOT_RECEIVE_ADENA_DISTRIBUTION
                    continue;
                }
                target.addAdena("Adena Distribution", memberAdenaGet, player, false);
                target.sendPacket(new ExDivideAdenaDone(party.isLeader(target), (commandChannel != null) && commandChannel.isLeader(target), _adenaCount, memberAdenaGet, targets.size(), player.getName()));
                target.removeRequest(AdenaDistributionRequest.class);
            }
        }
        else
        {
            cancelDistribution(request);
        }
    }

    private void cancelDistribution(AdenaDistributionRequest request)
    {
        request.getPlayers().stream().filter(Objects::nonNull).forEach(p ->
        {
            p.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
            p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
            p.removeRequest(AdenaDistributionRequest.class);
        });
    }
}