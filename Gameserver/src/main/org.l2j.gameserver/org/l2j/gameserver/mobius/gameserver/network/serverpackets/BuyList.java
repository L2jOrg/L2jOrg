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
import org.l2j.gameserver.mobius.gameserver.model.buylist.Product;
import org.l2j.gameserver.mobius.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Collection;

public final class BuyList extends AbstractItemPacket
{
	private final int _listId;
	private final Collection<Product> _list;
	private final long _money;
	private final int _inventorySlots;
	private final double _castleTaxRate;
	
	public BuyList(ProductList list, L2PcInstance player, double castleTaxRate)
	{
		_listId = list.getListId();
		_list = list.getProducts();
		_money = player.getAdena();
		_inventorySlots = player.getInventory().getItems((item) -> !item.isQuestItem()).size();
		_castleTaxRate = castleTaxRate;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BUY_SELL_LIST.writeId(packet);
		
		packet.writeD(0x00); // Type BUY
		packet.writeQ(_money); // current money
		packet.writeD(_listId);
		packet.writeD(_inventorySlots);
		packet.writeH(_list.size());
		for (Product product : _list)
		{
			if ((product.getCount() > 0) || !product.hasLimitedStock())
			{
				writeItem(packet, product);
				packet.writeQ((long) (product.getPrice() * (1.0 + _castleTaxRate + product.getBaseTaxRate())));
			}
		}
		return true;
	}
}
