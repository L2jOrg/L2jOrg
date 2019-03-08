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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.enums.ExManagePartyRoomMemberType;
import org.l2j.gameserver.mobius.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Gnacik
 */
public class ExManageMpccRoomMember implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final MatchingMemberType _memberType;
	private final ExManagePartyRoomMemberType _type;
	
	public ExManageMpccRoomMember(L2PcInstance player, CommandChannelMatchingRoom room, ExManagePartyRoomMemberType mode)
	{
		_activeChar = player;
		_memberType = room.getMemberType(player);
		_type = mode;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MANAGE_PARTY_ROOM_MEMBER.writeId(packet);
		
		packet.writeD(_type.ordinal());
		packet.writeD(_activeChar.getObjectId());
		packet.writeS(_activeChar.getName());
		packet.writeD(_activeChar.getClassId().getId());
		packet.writeD(_activeChar.getLevel());
		packet.writeD(MapRegionManager.getInstance().getBBs(_activeChar.getLocation()));
		packet.writeD(_memberType.ordinal());
		return true;
	}
}
