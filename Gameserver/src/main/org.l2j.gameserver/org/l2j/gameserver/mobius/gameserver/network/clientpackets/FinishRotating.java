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

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.StopRotation;

/**
 * This class ...
 * @version $Revision: 1.1.4.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class FinishRotating extends IClientIncomingPacket
{
	private int _degree;
	@SuppressWarnings("unused")
	private int _unknown;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_degree = packet.getInt();
		_unknown = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		if (!Config.ENABLE_KEYBOARD_MOVEMENT)
		{
			return;
		}
		
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		StopRotation sr;
		if (activeChar.isInAirShip() && activeChar.getAirShip().isCaptain(activeChar))
		{
			activeChar.getAirShip().setHeading(_degree);
			sr = new StopRotation(activeChar.getAirShip().getObjectId(), _degree, 0);
			activeChar.getAirShip().broadcastPacket(sr);
		}
		else
		{
			sr = new StopRotation(activeChar.getObjectId(), _degree, 0);
			activeChar.broadcastPacket(sr);
		}
	}
}
