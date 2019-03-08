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

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.mobius.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ListPartyWaiting;

public final class RequestPartyMatchConfig extends IClientIncomingPacket
{
	private int _page;
	private int _location;
	private PartyMatchingRoomLevelType _type;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_page = packet.getInt();
		_location = packet.getInt();
		_type = packet.getInt() == 0 ? PartyMatchingRoomLevelType.MY_LEVEL_RANGE : PartyMatchingRoomLevelType.ALL;
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		final L2Party party = activeChar.getParty();
		final L2CommandChannel cc = party == null ? null : party.getCommandChannel();
		
		if ((party != null) && (cc != null) && (cc.getLeader() == activeChar))
		{
			if (activeChar.getMatchingRoom() == null)
			{
				activeChar.setMatchingRoom(new CommandChannelMatchingRoom(activeChar.getName(), party.getDistributionType().ordinal(), 1, activeChar.getLevel(), 50, activeChar));
			}
		}
		else if ((cc != null) && (cc.getLeader() != activeChar))
		{
			activeChar.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_AFFILIATED_PARTY_S_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
		}
		else if ((party != null) && (party.getLeader() != activeChar))
		{
			activeChar.sendPacket(SystemMessageId.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
		}
		else
		{
			MatchingRoomManager.getInstance().addToWaitingList(activeChar);
			activeChar.sendPacket(new ListPartyWaiting(_type, _location, _page, activeChar.getLevel()));
		}
	}
}
