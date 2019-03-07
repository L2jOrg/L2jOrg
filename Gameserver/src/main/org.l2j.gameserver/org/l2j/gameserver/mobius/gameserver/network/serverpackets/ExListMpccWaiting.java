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

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.instancemanager.MatchingRoomManager;
import com.l2jmobius.gameserver.model.matching.MatchingRoom;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sdw
 */
public class ExListMpccWaiting implements IClientOutgoingPacket
{
	private static final int NUM_PER_PAGE = 64;
	private final int _size;
	private final List<MatchingRoom> _rooms = new LinkedList<>();
	
	public ExListMpccWaiting(int page, int location, int level)
	{
		final List<MatchingRoom> rooms = MatchingRoomManager.getInstance().getCCMathchingRooms(location, level);
		
		_size = rooms.size();
		final int startIndex = (page - 1) * NUM_PER_PAGE;
		int chunkSize = _size - startIndex;
		if (chunkSize > NUM_PER_PAGE)
		{
			chunkSize = NUM_PER_PAGE;
		}
		for (int i = startIndex; i < (startIndex + chunkSize); i++)
		{
			_rooms.add(rooms.get(i));
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_LIST_MPCC_WAITING.writeId(packet);
		
		packet.writeD(_size);
		packet.writeD(_rooms.size());
		for (MatchingRoom room : _rooms)
		{
			packet.writeD(room.getId());
			packet.writeS(room.getTitle());
			packet.writeD(room.getMembersCount());
			packet.writeD(room.getMinLvl());
			packet.writeD(room.getMaxLvl());
			packet.writeD(room.getLocation());
			packet.writeD(room.getMaxMembers());
			packet.writeS(room.getLeader().getName());
		}
		return true;
	}
}
