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
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.Calendar;

/**
 * @author Sdw
 */
public class ExFriendDetailInfo implements IClientOutgoingPacket
{
	private final int _objectId;
	private final L2PcInstance _friend;
	private final String _name;
	private final int _lastAccess;
	
	public ExFriendDetailInfo(L2PcInstance player, String name)
	{
		_objectId = player.getObjectId();
		_name = name;
		_friend = L2World.getInstance().getPlayer(_name);
		_lastAccess = _friend.isBlocked(player) ? 0 : _friend.isOnline() ? (int) System.currentTimeMillis() : (int) (System.currentTimeMillis() - _friend.getLastAccess()) / 1000;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_FRIEND_DETAIL_INFO.writeId(packet);
		
		packet.writeD(_objectId);
		
		if (_friend == null)
		{
			packet.writeS(_name);
			packet.writeD(0);
			packet.writeD(0);
			packet.writeH(0);
			packet.writeH(0);
			packet.writeD(0);
			packet.writeD(0);
			packet.writeS("");
			packet.writeD(0);
			packet.writeD(0);
			packet.writeS("");
			packet.writeD(1);
			packet.writeS(""); // memo
		}
		else
		{
			packet.writeS(_friend.getName());
			packet.writeD(_friend.isOnlineInt());
			packet.writeD(_friend.getObjectId());
			packet.writeH(_friend.getLevel());
			packet.writeH(_friend.getClassId().getId());
			packet.writeD(_friend.getClanId());
			packet.writeD(_friend.getClanCrestId());
			packet.writeS(_friend.getClan() != null ? _friend.getClan().getName() : "");
			packet.writeD(_friend.getAllyId());
			packet.writeD(_friend.getAllyCrestId());
			packet.writeS(_friend.getClan() != null ? _friend.getClan().getAllyName() : "");
			final Calendar createDate = _friend.getCreateDate();
			packet.writeC(createDate.get(Calendar.MONTH) + 1);
			packet.writeC(createDate.get(Calendar.DAY_OF_MONTH));
			packet.writeD(_lastAccess);
			packet.writeS(""); // memo
		}
		return true;
	}
}
