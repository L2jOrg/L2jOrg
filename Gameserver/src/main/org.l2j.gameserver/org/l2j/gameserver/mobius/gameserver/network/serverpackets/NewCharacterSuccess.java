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
import com.l2jmobius.gameserver.model.actor.templates.L2PcTemplate;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.List;

public final class NewCharacterSuccess implements IClientOutgoingPacket
{
	private final List<L2PcTemplate> _chars = new ArrayList<>();
	
	public void addChar(L2PcTemplate template)
	{
		_chars.add(template);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.NEW_CHARACTER_SUCCESS.writeId(packet);
		
		packet.writeD(_chars.size());
		for (L2PcTemplate chr : _chars)
		{
			if (chr == null)
			{
				continue;
			}
			
			// TODO: Unhardcode these
			packet.writeD(chr.getRace().ordinal());
			packet.writeD(chr.getClassId().getId());
			
			packet.writeD(99);
			packet.writeD(chr.getBaseSTR());
			packet.writeD(1);
			
			packet.writeD(99);
			packet.writeD(chr.getBaseDEX());
			packet.writeD(1);
			
			packet.writeD(99);
			packet.writeD(chr.getBaseCON());
			packet.writeD(1);
			
			packet.writeD(99);
			packet.writeD(chr.getBaseINT());
			packet.writeD(1);
			
			packet.writeD(99);
			packet.writeD(chr.getBaseWIT());
			packet.writeD(1);
			
			packet.writeD(99);
			packet.writeD(chr.getBaseMEN());
			packet.writeD(1);
		}
		return true;
	}
}
