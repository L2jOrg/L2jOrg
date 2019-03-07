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
import com.l2jmobius.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

public final class DoorStatusUpdate implements IClientOutgoingPacket
{
	private final L2DoorInstance _door;
	
	public DoorStatusUpdate(L2DoorInstance door)
	{
		_door = door;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.DOOR_STATUS_UPDATE.writeId(packet);
		
		packet.writeD(_door.getObjectId());
		packet.writeD(_door.isOpen() ? 0 : 1);
		packet.writeD(_door.getDamage());
		packet.writeD(_door.isEnemy() ? 1 : 0);
		packet.writeD(_door.getId());
		packet.writeD((int) _door.getCurrentHp());
		packet.writeD(_door.getMaxHp());
		return true;
	}
}