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

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.network.L2GameClient;

/**
 * Format: (c) dddd d: dx d: dy d: dz d: AirShip id ??
 * @author -Wooden-
 */
public class ExGetOnAirShip implements IClientIncomingPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _shipId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_x = packet.readD();
		_y = packet.readD();
		_z = packet.readD();
		_shipId = packet.readD();
		return false;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		LOGGER.info("[T1:ExGetOnAirShip] x: " + _x);
		LOGGER.info("[T1:ExGetOnAirShip] y: " + _y);
		LOGGER.info("[T1:ExGetOnAirShip] z: " + _z);
		LOGGER.info("[T1:ExGetOnAirShip] ship ID: " + _shipId);
	}
}
