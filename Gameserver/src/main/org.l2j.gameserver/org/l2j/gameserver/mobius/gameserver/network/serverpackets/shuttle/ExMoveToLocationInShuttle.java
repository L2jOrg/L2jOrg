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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.shuttle;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 */
public class ExMoveToLocationInShuttle implements IClientOutgoingPacket
{
	private final int _charObjId;
	private final int _airShipId;
	private final int _targetX;
	private final int _targetY;
	private final int _targetZ;
	private final int _fromX;
	private final int _fromY;
	private final int _fromZ;
	
	public ExMoveToLocationInShuttle(L2PcInstance player, int fromX, int fromY, int fromZ)
	{
		_charObjId = player.getObjectId();
		_airShipId = player.getShuttle().getObjectId();
		_targetX = player.getInVehiclePosition().getX();
		_targetY = player.getInVehiclePosition().getY();
		_targetZ = player.getInVehiclePosition().getZ();
		_fromX = fromX;
		_fromY = fromY;
		_fromZ = fromZ;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MOVE_TO_LOCATION_IN_SUTTLE.writeId(packet);
		
		packet.writeD(_charObjId);
		packet.writeD(_airShipId);
		packet.writeD(_targetX);
		packet.writeD(_targetY);
		packet.writeD(_targetZ);
		packet.writeD(_fromX);
		packet.writeD(_fromY);
		packet.writeD(_fromZ);
		return true;
	}
}
