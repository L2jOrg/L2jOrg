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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.attributechange;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.enums.AttributeType;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mobius
 */
public class ExChangeAttributeInfo implements IClientOutgoingPacket
{
	private static final Map<AttributeType, Byte> ATTRIBUTE_MASKS = new HashMap<>();
	{
		ATTRIBUTE_MASKS.put(AttributeType.FIRE, (byte) 1);
		ATTRIBUTE_MASKS.put(AttributeType.WATER, (byte) 2);
		ATTRIBUTE_MASKS.put(AttributeType.WIND, (byte) 4);
		ATTRIBUTE_MASKS.put(AttributeType.EARTH, (byte) 8);
		ATTRIBUTE_MASKS.put(AttributeType.HOLY, (byte) 16);
		ATTRIBUTE_MASKS.put(AttributeType.DARK, (byte) 32);
	}
	
	private final int _crystalItemId;
	private int _attributes;
	private int _itemObjId;
	
	public ExChangeAttributeInfo(int crystalItemId, L2ItemInstance item)
	{
		_crystalItemId = crystalItemId;
		_attributes = 0;
		for (AttributeType e : AttributeType.ATTRIBUTE_TYPES)
		{
			if (e != item.getAttackAttributeType())
			{
				_attributes |= ATTRIBUTE_MASKS.get(e);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CHANGE_ATTRIBUTE_INFO.writeId(packet);
		packet.writeD(_crystalItemId);
		packet.writeD(_attributes);
		packet.writeD(_itemObjId);
		return true;
	}
}