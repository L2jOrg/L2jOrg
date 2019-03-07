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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is made to create packets with any format
 * @author Maktakien
 */
public class AdminForgePacket implements IClientOutgoingPacket
{
	private final List<Part> _parts = new ArrayList<>();
	
	private static class Part
	{
		public byte b;
		public String str;
		
		public Part(byte bb, String string)
		{
			b = bb;
			str = string;
		}
	}
	
	public AdminForgePacket()
	{
		
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		for (Part p : _parts)
		{
			generate(packet, p.b, p.str);
		}
		return true;
	}
	
	/**
	 * @param packet
	 * @param type
	 * @param value
	 * @return
	 */
	public boolean generate(PacketWriter packet, byte type, String value)
	{
		if ((type == 'C') || (type == 'c'))
		{
			packet.writeC(Integer.decode(value));
			return true;
		}
		else if ((type == 'D') || (type == 'd'))
		{
			packet.writeD(Integer.decode(value));
			return true;
		}
		else if ((type == 'H') || (type == 'h'))
		{
			packet.writeH(Integer.decode(value));
			return true;
		}
		else if ((type == 'F') || (type == 'f'))
		{
			packet.writeF(Double.parseDouble(value));
			return true;
		}
		else if ((type == 'S') || (type == 's'))
		{
			packet.writeS(value);
			return true;
		}
		else if ((type == 'B') || (type == 'b') || (type == 'X') || (type == 'x'))
		{
			packet.writeB(new BigInteger(value).toByteArray());
			return true;
		}
		else if ((type == 'Q') || (type == 'q'))
		{
			packet.writeQ(Long.decode(value));
			return true;
		}
		return false;
	}
	
	public void addPart(byte b, String string)
	{
		_parts.add(new Part(b, string));
	}
}