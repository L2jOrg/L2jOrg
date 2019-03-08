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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.ClassId;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gnacik
 */
public class RequestListPartyMatchingWaitingRoom extends IClientIncomingPacket
{
	private int _page;
	private int _minLevel;
	private int _maxLevel;
	private List<ClassId> _classId; // 1 - waitlist 0 - room waitlist
	private String _query;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_page = packet.getInt();
		_minLevel = packet.getInt();
		_maxLevel = packet.getInt();
		final int size = packet.getInt();
		
		if ((size > 0) && (size < 128))
		{
			_classId = new LinkedList<>();
			for (int i = 0; i < size; i++)
			{
				_classId.add(ClassId.getClassId(packet.getInt()));
			}
		}
		if (packet.getReadableBytes() > 0)
		{
			_query = readString(packet);
		}
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
		
		client.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _page, _minLevel, _maxLevel, _classId, _query));
	}
}