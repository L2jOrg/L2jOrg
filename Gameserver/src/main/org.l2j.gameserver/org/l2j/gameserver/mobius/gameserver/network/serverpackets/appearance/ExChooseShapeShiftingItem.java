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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.appearance;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.items.appearance.AppearanceStone;
import com.l2jmobius.gameserver.model.items.appearance.AppearanceTargetType;
import com.l2jmobius.gameserver.model.items.appearance.AppearanceType;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 */
public class ExChooseShapeShiftingItem implements IClientOutgoingPacket
{
	private final AppearanceType _type;
	private final AppearanceTargetType _targetType;
	private final int _itemId;
	
	public ExChooseShapeShiftingItem(AppearanceStone stone)
	{
		_type = stone.getType();
		_targetType = stone.getTargetTypes().size() > 1 ? AppearanceTargetType.ALL : stone.getTargetTypes().get(0);
		_itemId = stone.getId();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CHOOSE_SHAPE_SHIFTING_ITEM.writeId(packet);
		
		packet.writeD(_targetType != null ? _targetType.ordinal() : 0);
		packet.writeD(_type != null ? _type.ordinal() : 0);
		packet.writeD(_itemId);
		return true;
	}
}
