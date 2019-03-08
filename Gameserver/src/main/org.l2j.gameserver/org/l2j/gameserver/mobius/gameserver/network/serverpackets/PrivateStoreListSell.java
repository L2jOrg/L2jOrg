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
import org.l2j.gameserver.mobius.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.mobius.gameserver.model.TradeItem;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class PrivateStoreListSell extends AbstractItemPacket
{
	private final L2PcInstance _player;
	private final L2PcInstance _seller;
	
	public PrivateStoreListSell(L2PcInstance player, L2PcInstance seller)
	{
		_player = player;
		_seller = seller;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_seller.isSellingBuffs())
		{
			SellBuffsManager.getInstance().sendBuffMenu(_player, _seller, 0);
		}
		else
		{
			OutgoingPackets.PRIVATE_STORE_LIST.writeId(packet);
			
			packet.writeD(_seller.getObjectId());
			packet.writeD(_seller.getSellList().isPackaged() ? 1 : 0);
			packet.writeQ(_player.getAdena());
			packet.writeD(0x00);
			packet.writeD(_seller.getSellList().getItems().length);
			for (TradeItem item : _seller.getSellList().getItems())
			{
				writeItem(packet, item);
				packet.writeQ(item.getPrice());
				packet.writeQ(item.getItem().getReferencePrice() * 2);
			}
		}
		return true;
	}
}
