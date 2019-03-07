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
package org.l2j.gameserver.mobius.gameserver.model.matching;

import com.l2jmobius.gameserver.enums.MatchingMemberType;
import com.l2jmobius.gameserver.enums.MatchingRoomType;
import com.l2jmobius.gameserver.enums.PartyMatchingRoomLevelType;
import com.l2jmobius.gameserver.enums.UserInfoType;
import com.l2jmobius.gameserver.instancemanager.MatchingRoomManager;
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.*;

/**
 * @author Sdw
 */
public final class PartyMatchingRoom extends MatchingRoom
{
	public PartyMatchingRoom(String title, int loot, int minlvl, int maxlvl, int maxmem, L2PcInstance leader)
	{
		super(title, loot, minlvl, maxlvl, maxmem, leader);
	}
	
	@Override
	protected void onRoomCreation(L2PcInstance player)
	{
		player.broadcastUserInfo(UserInfoType.CLAN);
		player.sendPacket(new ListPartyWaiting(PartyMatchingRoomLevelType.ALL, -1, 1, player.getLevel()));
		player.sendPacket(SystemMessageId.YOU_HAVE_CREATED_A_PARTY_ROOM);
	}
	
	@Override
	protected void notifyInvalidCondition(L2PcInstance player)
	{
		player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM);
	}
	
	@Override
	protected void notifyNewMember(L2PcInstance player)
	{
		// Update others player
		getMembers().stream().filter(p -> p != player).forEach(p ->
		{
			p.sendPacket(new ExPartyRoomMember(p, this));
		});
		
		// Send SystemMessage to others player
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_ENTERED_THE_PARTY_ROOM);
		sm.addPcName(player);
		getMembers().stream().filter(p -> p != player).forEach(sm::sendTo);
		
		// Update new player
		player.sendPacket(new PartyRoomInfo(this));
		player.sendPacket(new ExPartyRoomMember(player, this));
	}
	
	@Override
	protected void notifyRemovedMember(L2PcInstance player, boolean kicked, boolean leaderChanged)
	{
		final SystemMessage sm = SystemMessage.getSystemMessage(kicked ? SystemMessageId.C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM : SystemMessageId.C1_HAS_LEFT_THE_PARTY_ROOM);
		sm.addPcName(player);
		
		getMembers().forEach(p ->
		{
			p.sendPacket(new PartyRoomInfo(this));
			p.sendPacket(new ExPartyRoomMember(player, this));
			p.sendPacket(sm);
			p.sendPacket(SystemMessageId.THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED);
		});
		
		final SystemMessage sm2 = SystemMessage.getSystemMessage(kicked ? SystemMessageId.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMessageId.YOU_HAVE_EXITED_THE_PARTY_ROOM);
		player.sendPacket(sm2);
		player.sendPacket(ExClosePartyRoom.STATIC_PACKET);
	}
	
	@Override
	public void disbandRoom()
	{
		getMembers().forEach(p ->
		{
			p.sendPacket(SystemMessageId.THE_PARTY_ROOM_HAS_BEEN_DISBANDED);
			p.sendPacket(ExClosePartyRoom.STATIC_PACKET);
			p.setMatchingRoom(null);
			p.broadcastUserInfo(UserInfoType.CLAN);
			MatchingRoomManager.getInstance().addToWaitingList(p);
		});
		
		getMembers().clear();
		
		MatchingRoomManager.getInstance().removeMatchingRoom(this);
	}
	
	@Override
	public MatchingRoomType getRoomType()
	{
		return MatchingRoomType.PARTY;
	}
	
	@Override
	public MatchingMemberType getMemberType(L2PcInstance player)
	{
		if (isLeader(player))
		{
			return MatchingMemberType.PARTY_LEADER;
		}
		
		final L2Party leaderParty = getLeader().getParty();
		final L2Party playerParty = player.getParty();
		if ((leaderParty != null) && (playerParty != null) && (playerParty == leaderParty))
		{
			return MatchingMemberType.PARTY_MEMBER;
		}
		
		return MatchingMemberType.WAITING_PLAYER;
	}
}
