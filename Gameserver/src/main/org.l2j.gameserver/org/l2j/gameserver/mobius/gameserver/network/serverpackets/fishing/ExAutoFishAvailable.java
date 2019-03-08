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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.fishing;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author bit
 */
public class ExAutoFishAvailable implements IClientOutgoingPacket
{
	public static ExAutoFishAvailable YES = new ExAutoFishAvailable(true);
	public static ExAutoFishAvailable NO = new ExAutoFishAvailable(false);
	
	private final boolean _available;
	
	private ExAutoFishAvailable(boolean available)
	{
		_available = available;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_AUTO_FISH_AVAILABLE.writeId(packet);
		packet.writeC(_available ? 1 : 0);
		return true;
	}
}
