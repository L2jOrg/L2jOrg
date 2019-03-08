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
import org.l2j.gameserver.mobius.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.mobius.gameserver.model.matching.PartyMatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.PartyRoomInfo;

/**
 * author: Gnacik
 */
public class RequestPartyMatchList extends IClientIncomingPacket
{
	private int _roomId;
	private int _maxMembers;
	private int _minLevel;
	private int _maxLevel;
	private int _lootType;
	private String _roomTitle;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_roomId = packet.getInt();
		_maxMembers = packet.getInt();
		_minLevel = packet.getInt();
		_maxLevel = packet.getInt();
		_lootType = packet.getInt();
		_roomTitle = readString(packet);
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
		
		if ((_roomId <= 0) && (activeChar.getMatchingRoom() == null))
		{
			final PartyMatchingRoom room = new PartyMatchingRoom(_roomTitle, _lootType, _minLevel, _maxLevel, _maxMembers, activeChar);
			activeChar.setMatchingRoom(room);
		}
		else
		{
			final MatchingRoom room = activeChar.getMatchingRoom();
			if ((room.getId() == _roomId) && (room.getRoomType() == MatchingRoomType.PARTY) && room.isLeader(activeChar))
			{
				room.setLootType(_lootType);
				room.setMinLvl(_minLevel);
				room.setMaxLvl(_maxLevel);
				room.setMaxMembers(_maxMembers);
				room.setTitle(_roomTitle);
				
				final PartyRoomInfo packet = new PartyRoomInfo((PartyMatchingRoom) room);
				room.getMembers().forEach(packet::sendTo);
			}
		}
	}
	
}
