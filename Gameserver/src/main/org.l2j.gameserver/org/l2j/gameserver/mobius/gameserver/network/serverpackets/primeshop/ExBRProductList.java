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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.primeshop;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.primeshop.PrimeShopGroup;
import com.l2jmobius.gameserver.model.primeshop.PrimeShopItem;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.Collection;

/**
 * @author UnAfraid
 */
public class ExBRProductList implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final int _type;
	private final Collection<PrimeShopGroup> _primeList;
	
	public ExBRProductList(L2PcInstance activeChar, int type, Collection<PrimeShopGroup> items)
	{
		_activeChar = activeChar;
		_type = type;
		_primeList = items;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BR_PRODUCT_LIST.writeId(packet);
		
		packet.writeQ(_activeChar.getAdena()); // Adena
		packet.writeQ(0x00); // Hero coins
		packet.writeC(_type); // Type 0 - Home, 1 - History, 2 - Favorites
		packet.writeD(_primeList.size());
		for (PrimeShopGroup brItem : _primeList)
		{
			packet.writeD(brItem.getBrId());
			packet.writeC(brItem.getCat());
			packet.writeC(brItem.getPaymentType()); // Payment Type: 0 - Prime Points, 1 - Adena, 2 - Hero Coins
			packet.writeD(brItem.getPrice());
			packet.writeC(brItem.getPanelType()); // Item Panel Type: 0 - None, 1 - Event, 2 - Sale, 3 - New, 4 - Best
			packet.writeD(brItem.getRecommended()); // Recommended: (bit flags) 1 - Top, 2 - Left, 4 - Right
			packet.writeD(brItem.getStartSale());
			packet.writeD(brItem.getEndSale());
			packet.writeC(brItem.getDaysOfWeek());
			packet.writeC(brItem.getStartHour());
			packet.writeC(brItem.getStartMinute());
			packet.writeC(brItem.getStopHour());
			packet.writeC(brItem.getStopMinute());
			packet.writeD(brItem.getStock());
			packet.writeD(brItem.getTotal());
			packet.writeC(brItem.getSalePercent());
			packet.writeC(brItem.getMinLevel());
			packet.writeC(brItem.getMaxLevel());
			packet.writeD(brItem.getMinBirthday());
			packet.writeD(brItem.getMaxBirthday());
			packet.writeD(brItem.getRestrictionDay());
			packet.writeD(brItem.getAvailableCount());
			packet.writeC(brItem.getItems().size());
			for (PrimeShopItem item : brItem.getItems())
			{
				packet.writeD(item.getId());
				packet.writeD((int) item.getCount());
				packet.writeD(item.getWeight());
				packet.writeD(item.isTradable());
			}
		}
		return true;
	}
}