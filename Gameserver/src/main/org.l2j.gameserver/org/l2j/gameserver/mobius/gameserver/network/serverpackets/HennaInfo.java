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
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.L2Henna;
import com.l2jmobius.gameserver.model.stats.BaseStats;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.List;

/**
 * This server packet sends the player's henna information.
 * @author Zoey76
 */
public final class HennaInfo implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final List<L2Henna> _hennas = new ArrayList<>();
	
	public HennaInfo(L2PcInstance player)
	{
		_activeChar = player;
		for (L2Henna henna : _activeChar.getHennaList())
		{
			if (henna != null)
			{
				_hennas.add(henna);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.HENNA_INFO.writeId(packet);
		
		packet.writeH(_activeChar.getHennaValue(BaseStats.INT)); // equip INT
		packet.writeH(_activeChar.getHennaValue(BaseStats.STR)); // equip STR
		packet.writeH(_activeChar.getHennaValue(BaseStats.CON)); // equip CON
		packet.writeH(_activeChar.getHennaValue(BaseStats.MEN)); // equip MEN
		packet.writeH(_activeChar.getHennaValue(BaseStats.DEX)); // equip DEX
		packet.writeH(_activeChar.getHennaValue(BaseStats.WIT)); // equip WIT
		packet.writeH(0x00); // equip LUC
		packet.writeH(0x00); // equip CHA
		packet.writeD(3 - _activeChar.getHennaEmptySlots()); // Slots
		packet.writeD(_hennas.size()); // Size
		for (L2Henna henna : _hennas)
		{
			packet.writeD(henna.getDyeId());
			packet.writeD(henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00);
		}
		packet.writeD(0x00); // Premium Slot Dye ID
		packet.writeD(0x00); // Premium Slot Dye Time Left
		packet.writeD(0x00); // Premium Slot Dye ID isValid
		return true;
	}
}
