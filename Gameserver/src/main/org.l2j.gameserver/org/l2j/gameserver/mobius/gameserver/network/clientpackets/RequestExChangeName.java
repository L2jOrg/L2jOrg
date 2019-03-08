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

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExNeedToChangeName;

/**
 * Reply for {@link ExNeedToChangeName}
 * @author JIV
 */
public class RequestExChangeName extends IClientIncomingPacket
{
	private String _newName;
	private int _type;
	private int _charSlot;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_type = packet.getInt();
		_newName = readString(packet);
		_charSlot = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		LOGGER.info("Recieved " + getClass().getSimpleName() + " name: " + _newName + " type: " + _type + " CharSlot: " + _charSlot);
	}
}
