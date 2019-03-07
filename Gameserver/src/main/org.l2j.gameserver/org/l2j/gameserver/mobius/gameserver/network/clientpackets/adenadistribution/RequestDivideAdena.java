/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.adenadistribution;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.model.L2CommandChannel;
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.actor.request.AdenaDistributionRequest;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;
import com.l2jmobius.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaDone;

import java.util.List;
import java.util.Objects;

/**
 * @author Sdw
 */
public class RequestDivideAdena implements IClientIncomingPacket
{
	private int _adenaObjId;
	private long _adenaCount;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_adenaObjId = packet.readD();
		_adenaCount = packet.readQ();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
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
		
		final long memberAdenaGet = (long) Math.floor(_adenaCount / targets.size());
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