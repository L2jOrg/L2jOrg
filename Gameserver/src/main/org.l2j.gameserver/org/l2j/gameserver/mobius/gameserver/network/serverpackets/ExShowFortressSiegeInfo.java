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
import org.l2j.gameserver.mobius.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.mobius.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.mobius.gameserver.model.entity.Fort;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * TODO: Rewrite!!!
 * @author KenM
 */
public class ExShowFortressSiegeInfo implements IClientOutgoingPacket
{
	private final int _fortId;
	private final int _size;
	private final int _csize;
	private final int _csize2;
	
	/**
	 * @param fort
	 */
	public ExShowFortressSiegeInfo(Fort fort)
	{
		_fortId = fort.getResidenceId();
		_size = fort.getFortSize();
		final List<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(_fortId);
		_csize = ((commanders == null) ? 0 : commanders.size());
		_csize2 = fort.getSiege().getCommanders().size();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SHOW_FORTRESS_SIEGE_INFO.writeId(packet);
		
		packet.writeD(_fortId); // Fortress Id
		packet.writeD(_size); // Total Barracks Count
		if (_csize > 0)
		{
			switch (_csize)
			{
				case 3:
				{
					switch (_csize2)
					{
						case 0:
						{
							packet.writeD(0x03);
							break;
						}
						case 1:
						{
							packet.writeD(0x02);
							break;
						}
						case 2:
						{
							packet.writeD(0x01);
							break;
						}
						case 3:
						{
							packet.writeD(0x00);
							break;
						}
					}
					break;
				}
				case 4: // TODO: change 4 to 5 once control room supported
				{
					switch (_csize2)
					{
						// TODO: once control room supported, update packet.writeD(0x0x) to support 5th room
						case 0:
						{
							packet.writeD(0x05);
							break;
						}
						case 1:
						{
							packet.writeD(0x04);
							break;
						}
						case 2:
						{
							packet.writeD(0x03);
							break;
						}
						case 3:
						{
							packet.writeD(0x02);
							break;
						}
						case 4:
						{
							packet.writeD(0x01);
							break;
						}
					}
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < _size; i++)
			{
				packet.writeD(0x00);
			}
		}
		return true;
	}
}
