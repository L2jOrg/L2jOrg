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
import org.l2j.gameserver.mobius.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.mobius.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Steuf, UnAfraid
 */
public class AgitDecoInfo implements IClientOutgoingPacket
{
	private final AbstractResidence _residense;
	
	public AgitDecoInfo(AbstractResidence residense)
	{
		_residense = residense;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.AGIT_DECO_INFO.writeId(packet);
		packet.writeD(_residense.getResidenceId());
		for (ResidenceFunctionType type : ResidenceFunctionType.values())
		{
			if (type == ResidenceFunctionType.NONE)
			{
				continue;
			}
			packet.writeC(_residense.hasFunction(type) ? 0x01 : 0x00);
		}
		
		// Unknown
		packet.writeD(0); // TODO: Find me!
		packet.writeD(0); // TODO: Find me!
		packet.writeD(0); // TODO: Find me!
		packet.writeD(0); // TODO: Find me!
		packet.writeD(0); // TODO: Find me!
		return true;
	}
}
