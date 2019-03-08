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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class RecipeShopSellList implements IClientOutgoingPacket
{
	private final L2PcInstance _buyer;
	private final L2PcInstance _manufacturer;
	
	public RecipeShopSellList(L2PcInstance buyer, L2PcInstance manufacturer)
	{
		_buyer = buyer;
		_manufacturer = manufacturer;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RECIPE_SHOP_SELL_LIST.writeId(packet);
		
		packet.writeD(_manufacturer.getObjectId());
		packet.writeD((int) _manufacturer.getCurrentMp()); // Creator's MP
		packet.writeD(_manufacturer.getMaxMp()); // Creator's MP
		packet.writeQ(_buyer.getAdena()); // Buyer Adena
		if (!_manufacturer.hasManufactureShop())
		{
			packet.writeD(0x00);
		}
		else
		{
			packet.writeD(_manufacturer.getManufactureItems().size());
			for (L2ManufactureItem temp : _manufacturer.getManufactureItems().values())
			{
				packet.writeD(temp.getRecipeId());
				packet.writeD(0x00); // unknown
				packet.writeQ(temp.getCost());
			}
		}
		return true;
	}
}
