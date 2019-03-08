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
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.CropProcure;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.HashMap;
import java.util.Map;

public class SellListProcure implements IClientOutgoingPacket
{
	private final long _money;
	private final Map<L2ItemInstance, Long> _sellList = new HashMap<>();
	
	public SellListProcure(L2PcInstance player, int castleId)
	{
		_money = player.getAdena();
		for (CropProcure c : CastleManorManager.getInstance().getCropProcure(castleId, false))
		{
			final L2ItemInstance item = player.getInventory().getItemByItemId(c.getId());
			if ((item != null) && (c.getAmount() > 0))
			{
				_sellList.put(item, c.getAmount());
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SELL_LIST_PROCURE.writeId(packet);
		
		packet.writeQ(_money); // money
		packet.writeD(0x00); // lease ?
		packet.writeH(_sellList.size()); // list size
		
		for (L2ItemInstance item : _sellList.keySet())
		{
			packet.writeH(item.getItem().getType1());
			packet.writeD(item.getObjectId());
			packet.writeD(item.getDisplayId());
			packet.writeQ(_sellList.get(item)); // count
			packet.writeH(item.getItem().getType2());
			packet.writeH(0); // unknown
			packet.writeQ(0); // price, u shouldnt get any adena for crops, only raw materials
		}
		return true;
	}
}
