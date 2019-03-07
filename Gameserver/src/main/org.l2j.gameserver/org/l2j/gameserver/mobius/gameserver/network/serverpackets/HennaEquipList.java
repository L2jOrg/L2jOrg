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
import com.l2jmobius.gameserver.data.xml.impl.HennaData;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.L2Henna;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author Zoey76
 */
public class HennaEquipList implements IClientOutgoingPacket
{
	private final L2PcInstance _player;
	private final List<L2Henna> _hennaEquipList;
	
	public HennaEquipList(L2PcInstance player)
	{
		_player = player;
		_hennaEquipList = HennaData.getInstance().getHennaList(player.getClassId());
	}
	
	public HennaEquipList(L2PcInstance player, List<L2Henna> list)
	{
		_player = player;
		_hennaEquipList = list;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.HENNA_EQUIP_LIST.writeId(packet);
		packet.writeQ(_player.getAdena()); // activeChar current amount of Adena
		packet.writeD(3); // available equip slot
		packet.writeD(_hennaEquipList.size());
		
		for (L2Henna henna : _hennaEquipList)
		{
			// Player must have at least one dye in inventory
			// to be able to see the Henna that can be applied with it.
			if ((_player.getInventory().getItemByItemId(henna.getDyeItemId())) != null)
			{
				packet.writeD(henna.getDyeId()); // dye Id
				packet.writeD(henna.getDyeItemId()); // item Id of the dye
				packet.writeQ(henna.getWearCount()); // amount of dyes required
				packet.writeQ(henna.getWearFee()); // amount of Adena required
				packet.writeD(henna.isAllowedClass(_player.getClassId()) ? 0x01 : 0x00); // meet the requirement or not
				// packet.writeD(0x00); // Does not exist in Classic.
			}
		}
		return true;
	}
}
