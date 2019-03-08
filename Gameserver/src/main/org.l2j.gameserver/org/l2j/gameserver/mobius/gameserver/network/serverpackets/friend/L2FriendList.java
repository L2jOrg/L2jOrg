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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.friend;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.LinkedList;
import java.util.List;

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 * @author Tempy
 */
public class L2FriendList implements IClientOutgoingPacket
{
	private final List<FriendInfo> _info = new LinkedList<>();
	
	private static class FriendInfo
	{
		int _objId;
		String _name;
		int _level;
		int _classId;
		boolean _online;
		
		public FriendInfo(int objId, String name, boolean online, int level, int classId)
		{
			_objId = objId;
			_name = name;
			_online = online;
			_level = level;
			_classId = classId;
		}
	}
	
	public L2FriendList(L2PcInstance player)
	{
		for (int objId : player.getFriendList())
		{
			final String name = CharNameTable.getInstance().getNameById(objId);
			final L2PcInstance player1 = L2World.getInstance().getPlayer(objId);
			boolean online = false;
			int level = 0;
			int classId = 0;
			
			if (player1 != null)
			{
				online = true;
				level = player1.getLevel();
				classId = player1.getClassId().getId();
			}
			else
			{
				level = CharNameTable.getInstance().getLevelById(objId);
				classId = CharNameTable.getInstance().getClassIdById(objId);
			}
			_info.add(new FriendInfo(objId, name, online, level, classId));
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.L2_FRIEND_LIST.writeId(packet);
		
		packet.writeD(_info.size());
		for (FriendInfo info : _info)
		{
			packet.writeD(info._objId); // character id
			packet.writeS(info._name);
			packet.writeD(info._online ? 0x01 : 0x00); // online
			packet.writeD(info._online ? info._objId : 0x00); // object id if online
			packet.writeD(info._level);
			packet.writeD(info._classId);
			packet.writeH(0x00);
		}
		return true;
	}
}