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
import org.l2j.gameserver.mobius.gameserver.model.L2ManufactureItem;
import org.l2j.gameserver.mobius.gameserver.model.L2RecipeList;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Iterator;

public class RecipeShopManageList implements IClientOutgoingPacket
{
	private final L2PcInstance _seller;
	private final boolean _isDwarven;
	private L2RecipeList[] _recipes;
	
	public RecipeShopManageList(L2PcInstance seller, boolean isDwarven)
	{
		_seller = seller;
		_isDwarven = isDwarven;
		
		if (_isDwarven && _seller.hasDwarvenCraft())
		{
			_recipes = _seller.getDwarvenRecipeBook();
		}
		else
		{
			_recipes = _seller.getCommonRecipeBook();
		}
		
		if (_seller.hasManufactureShop())
		{
			final Iterator<L2ManufactureItem> it = _seller.getManufactureItems().values().iterator();
			L2ManufactureItem item;
			while (it.hasNext())
			{
				item = it.next();
				if ((item.isDwarven() != _isDwarven) || !seller.hasRecipeList(item.getRecipeId()))
				{
					it.remove();
				}
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RECIPE_SHOP_MANAGE_LIST.writeId(packet);
		
		packet.writeD(_seller.getObjectId());
		packet.writeD((int) _seller.getAdena());
		packet.writeD(_isDwarven ? 0x00 : 0x01);
		
		if (_recipes == null)
		{
			packet.writeD(0);
		}
		else
		{
			packet.writeD(_recipes.length); // number of items in recipe book
			
			for (int i = 0; i < _recipes.length; i++)
			{
				final L2RecipeList temp = _recipes[i];
				packet.writeD(temp.getId());
				packet.writeD(i + 1);
			}
		}
		
		if (!_seller.hasManufactureShop())
		{
			packet.writeD(0x00);
		}
		else
		{
			packet.writeD(_seller.getManufactureItems().size());
			for (L2ManufactureItem item : _seller.getManufactureItems().values())
			{
				packet.writeD(item.getRecipeId());
				packet.writeD(0x00);
				packet.writeQ(item.getCost());
			}
		}
		return true;
	}
}
