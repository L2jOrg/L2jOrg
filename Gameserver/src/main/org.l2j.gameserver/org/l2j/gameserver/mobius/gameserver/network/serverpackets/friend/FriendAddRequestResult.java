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

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 */
public class FriendAddRequestResult implements IClientOutgoingPacket
{
	private final int _result;
	private final int _charId;
	private final String _charName;
	private final int _isOnline;
	private final int _charObjectId;
	private final int _charLevel;
	private final int _charClassId;
	
	public FriendAddRequestResult(L2PcInstance activeChar, int result)
	{
		_result = result;
		_charId = activeChar.getObjectId();
		_charName = activeChar.getName();
		_isOnline = activeChar.isOnlineInt();
		_charObjectId = activeChar.getObjectId();
		_charLevel = activeChar.getLevel();
		_charClassId = activeChar.getActiveClass();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.FRIEND_ADD_REQUEST_RESULT.writeId(packet);
		
		packet.writeD(_result);
		packet.writeD(_charId);
		packet.writeS(_charName);
		packet.writeD(_isOnline);
		packet.writeD(_charObjectId);
		packet.writeD(_charLevel);
		packet.writeD(_charClassId);
		packet.writeH(0x00); // Always 0 on retail
		return true;
	}
}
