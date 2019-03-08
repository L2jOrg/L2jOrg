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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2DoorInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public final class DoorInfo implements IClientOutgoingPacket
{
	private final L2DoorInstance _door;
	
	public DoorInfo(L2DoorInstance door)
	{
		_door = door;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.DOOR_INFO.writeId(packet);
		
		packet.writeD(_door.getObjectId());
		packet.writeD(_door.getId());
		return true;
	}
}
