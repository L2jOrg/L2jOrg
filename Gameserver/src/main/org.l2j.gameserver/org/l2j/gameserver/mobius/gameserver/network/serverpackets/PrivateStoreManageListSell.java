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
import org.l2j.gameserver.mobius.gameserver.model.TradeItem;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Collection;

public class PrivateStoreManageListSell extends AbstractItemPacket
{
	private final int _sendType;
	private final int _objId;
	private final long _playerAdena;
	private final boolean _packageSale;
	private final Collection<TradeItem> _itemList;
	private final TradeItem[] _sellList;
	
	public PrivateStoreManageListSell(int sendType, L2PcInstance player, boolean isPackageSale)
	{
		_sendType = sendType;
		_objId = player.getObjectId();
		_playerAdena = player.getAdena();
		player.getSellList().updateItems();
		_packageSale = isPackageSale;
		_itemList = player.getInventory().getAvailableItems(player.getSellList());
		_sellList = player.getSellList().getItems();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PRIVATE_STORE_MANAGE_LIST.writeId(packet);
		packet.writeC(_sendType);
		if (_sendType == 2)
		{
			packet.writeD(_itemList.size());
			packet.writeD(_itemList.size());
			for (TradeItem item : _itemList)
			{
				writeItem(packet, item);
				packet.writeQ(item.getItem().getReferencePrice() * 2);
			}
		}
		else
		{
			packet.writeD(_objId);
			packet.writeD(_packageSale ? 1 : 0);
			packet.writeQ(_playerAdena);
			packet.writeD(0x00);
			for (TradeItem item : _itemList)
			{
				writeItem(packet, item);
				packet.writeQ(item.getItem().getReferencePrice() * 2);
			}
			packet.writeD(0x00);
			for (TradeItem item2 : _sellList)
			{
				writeItem(packet, item2);
				packet.writeQ(item2.getPrice());
				packet.writeQ(item2.getItem().getReferencePrice() * 2);
			}
		}
		return true;
	}
}
