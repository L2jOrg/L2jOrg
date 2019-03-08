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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.L2Party.MessageType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.PartyRequest;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.JoinParty;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

public final class RequestAnswerJoinParty extends IClientIncomingPacket
{
	private int _response;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_response = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final PartyRequest request = player.getRequest(PartyRequest.class);
		if ((request == null) || request.isProcessing() || !player.removeRequest(request.getClass()))
		{
			return;
		}
		request.setProcessing(true);
		
		final L2PcInstance requestor = request.getActiveChar();
		if (requestor == null)
		{
			return;
		}
		
		final L2Party party = request.getParty();
		final L2Party requestorParty = requestor.getParty();
		if ((requestorParty != null) && (requestorParty != party))
		{
			return;
		}
		
		requestor.sendPacket(new JoinParty(_response));
		
		if (_response == 1)
		{
			if (party.getMemberCount() >= Config.ALT_PARTY_MAX_MEMBERS)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_PARTY_IS_FULL);
				player.sendPacket(sm);
				requestor.sendPacket(sm);
				return;
			}
			
			// Assign the party to the leader upon accept of his partner
			if (requestorParty == null)
			{
				requestor.setParty(party);
			}
			
			player.joinParty(party);
			
			final MatchingRoom requestorRoom = requestor.getMatchingRoom();
			
			if (requestorRoom != null)
			{
				requestorRoom.addMember(player);
			}
		}
		else if (_response == -1)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_PARTY_REQUESTS_AND_CANNOT_RECEIVE_A_PARTY_REQUEST);
			sm.addPcName(player);
			requestor.sendPacket(sm);
			
			if (party.getMemberCount() == 1)
			{
				party.removePartyMember(requestor, MessageType.NONE);
			}
		}
		else if (party.getMemberCount() == 1)
		{
			party.removePartyMember(requestor, MessageType.NONE);
		}
		
		party.setPendingInvitation(false);
		request.setProcessing(false);
	}
}
