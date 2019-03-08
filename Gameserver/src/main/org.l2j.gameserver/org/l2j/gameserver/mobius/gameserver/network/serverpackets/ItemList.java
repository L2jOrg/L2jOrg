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
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;
import java.util.stream.Collectors;

public final class ItemList extends AbstractItemPacket
{
	private final int _sendType;
	private final L2PcInstance _activeChar;
	private final List<L2ItemInstance> _items;
	
	public ItemList(int sendType, L2PcInstance activeChar)
	{
		_sendType = sendType;
		_activeChar = activeChar;
		_items = activeChar.getInventory().getItems(item -> !item.isQuestItem()).stream().collect(Collectors.toList());
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ITEM_LIST.writeId(packet);
		if (_sendType == 2)
		{
			packet.writeC(_sendType);
			packet.writeD(_items.size());
			packet.writeD(_items.size());
			for (L2ItemInstance item : _items)
			{
				writeItem(packet, item);
			}
		}
		else
		{
			packet.writeC(0x01); // _showWindow ? 0x01 : 0x00
			packet.writeD(0x00);
			packet.writeD(_items.size());
		}
		writeInventoryBlock(packet, _activeChar.getInventory());
		return true;
	}
}
