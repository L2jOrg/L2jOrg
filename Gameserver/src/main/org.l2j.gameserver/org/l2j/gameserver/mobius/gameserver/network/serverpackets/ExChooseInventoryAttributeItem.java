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
import org.l2j.gameserver.mobius.gameserver.enums.AttributeType;
import org.l2j.gameserver.mobius.gameserver.model.Elementals;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem implements IClientOutgoingPacket
{
	private final int _itemId;
	private final long _count;
	private final AttributeType _atribute;
	private final int _level;
	private final Set<Integer> _items = new HashSet<>();
	
	public ExChooseInventoryAttributeItem(L2PcInstance activeChar, L2ItemInstance stone)
	{
		_itemId = stone.getDisplayId();
		_count = stone.getCount();
		_atribute = AttributeType.findByClientId(Elementals.getItemElement(_itemId));
		if ((_atribute == AttributeType.NONE) || (_atribute == AttributeType.NONE_ARMOR))
		{
			throw new IllegalArgumentException("Undefined Atribute item: " + stone);
		}
		_level = Elementals.getMaxElementLevel(_itemId);
		
		// Register only items that can be put an attribute stone/crystal
		for (L2ItemInstance item : activeChar.getInventory().getItems())
		{
			if (item.isElementable())
			{
				_items.add(item.getObjectId());
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM.writeId(packet);
		
		packet.writeD(_itemId);
		packet.writeQ(_count);
		packet.writeD(_atribute == AttributeType.FIRE ? 1 : 0); // Fire
		packet.writeD(_atribute == AttributeType.WATER ? 1 : 0); // Water
		packet.writeD(_atribute == AttributeType.WIND ? 1 : 0); // Wind
		packet.writeD(_atribute == AttributeType.EARTH ? 1 : 0); // Earth
		packet.writeD(_atribute == AttributeType.HOLY ? 1 : 0); // Holy
		packet.writeD(_atribute == AttributeType.DARK ? 1 : 0); // Unholy
		packet.writeD(_level); // Item max attribute level
		packet.writeD(_items.size());
		_items.forEach(packet::writeD);
		return true;
	}
}
