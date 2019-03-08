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

import org.l2j.commons.network.IIncomingPacket;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.network.ExIncomingPackets;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * @author Nos
 */
public class ExPacket extends IClientIncomingPacket
{
	// private static final Logger LOGGER = Logger.getLogger(ExPacket.class.getName());
	
	private
	ExIncomingPackets _exIncomingPacket;
	private IIncomingPacket<L2GameClient> _exPacket;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		final int exPacketId = packet.getShort() & 0xFFFF;
		if ((exPacketId < 0) || (exPacketId >= ExIncomingPackets.PACKET_ARRAY.length))
		{
			return false;
		}
		
		_exIncomingPacket = ExIncomingPackets.PACKET_ARRAY[exPacketId];
		if (_exIncomingPacket == null)
		{
			// LOGGER.finer(getClass().getSimpleName() + ": Unknown packet: " + Integer.toHexString(exPacketId));
			return false;
		}
		
		_exPacket = _exIncomingPacket.newIncomingPacket();
		return (_exPacket != null) && _exPacket.read(client, packet);
	}
	
	@Override
	public void runImpl() throws Exception
	{
		if (!_exIncomingPacket.getConnectionStates().contains(client.getConnectionState()))
		{
			// LOGGER.finer(_exIncomingPacket + ": Connection at invalid state: " + client.getConnectionState() + " Required State: " + _exIncomingPacket.getConnectionStates());
			return;
		}
		_exPacket.run(client);
	}
}
