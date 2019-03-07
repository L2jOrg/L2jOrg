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
import com.l2jmobius.gameserver.model.actor.L2Summon;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.Collection;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends AbstractItemPacket
{
	private Collection<L2ItemInstance> _sellList;
	private Collection<L2ItemInstance> _refundList = null;
	private final boolean _done;
	private final int _inventorySlots;
	private double _castleTaxRate = 1;
	
	public ExBuySellList(L2PcInstance player, boolean done)
	{
		final L2Summon pet = player.getPet();
		_sellList = player.getInventory().getItems(item -> !item.isEquipped() && item.isSellable() && ((pet == null) || (item.getObjectId() != pet.getControlObjectId())));
		_inventorySlots = player.getInventory().getItems((item) -> !item.isQuestItem()).size();
		if (player.hasRefund())
		{
			_refundList = player.getRefund().getItems();
		}
		_done = done;
	}
	
	public ExBuySellList(L2PcInstance player, boolean done, double castleTaxRate)
	{
		this(player, done);
		_castleTaxRate = 1 - castleTaxRate;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BUY_SELL_LIST.writeId(packet);
		
		packet.writeD(0x01); // Type SELL
		packet.writeD(_inventorySlots);
		
		if ((_sellList != null))
		{
			packet.writeH(_sellList.size());
			for (L2ItemInstance item : _sellList)
			{
				writeItem(packet, item);
				packet.writeQ((long) ((item.getItem().getReferencePrice() / 2) * _castleTaxRate));
			}
		} 
		else
		{
			packet.writeH(0x00);
		}
		
		if ((_refundList != null) && !_refundList.isEmpty()) 
		{
			packet.writeH(_refundList.size());
			int i = 0;
			for (L2ItemInstance item : _refundList)
			{
				writeItem(packet, item);
				packet.writeD(i++);
				packet.writeQ((item.getItem().getReferencePrice() / 2) * item.getCount());
			}
		}
		else
		{
			packet.writeH(0x00);
		}
		
		packet.writeC(_done ? 0x01 : 0x00);
		return true;
	}
}
