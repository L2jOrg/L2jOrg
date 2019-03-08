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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Zoey76
 */
public class HennaRemoveList implements IClientOutgoingPacket
{
	private final L2PcInstance _player;
	
	public HennaRemoveList(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.HENNA_UNEQUIP_LIST.writeId(packet);
		
		packet.writeQ(_player.getAdena());
		packet.writeD(0x03); // seems to be max size
		packet.writeD(3 - _player.getHennaEmptySlots());
		
		for (L2Henna henna : _player.getHennaList())
		{
			if (henna != null)
			{
				packet.writeD(henna.getDyeId());
				packet.writeD(henna.getDyeItemId());
				packet.writeQ(henna.getCancelCount());
				packet.writeQ(henna.getCancelFee());
				packet.writeD(0x00);
			}
		}
		return true;
	}
}
