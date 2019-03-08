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
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class ExPutItemResultForVariationCancel implements IClientOutgoingPacket
{
	private final int _itemObjId;
	private final int _itemId;
	private final int _itemAug1;
	private final int _itemAug2;
	private final long _price;
	
	public ExPutItemResultForVariationCancel(L2ItemInstance item, long price)
	{
		_itemObjId = item.getObjectId();
		_itemId = item.getDisplayId();
		_price = price;
		_itemAug1 = item.getAugmentation().getOption1Id();
		_itemAug2 = item.getAugmentation().getOption2Id();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PUT_ITEM_RESULT_FOR_VARIATION_CANCEL.writeId(packet);
		
		packet.writeD(_itemObjId);
		packet.writeD(_itemId);
		packet.writeD(_itemAug1);
		packet.writeD(_itemAug2);
		packet.writeQ(_price);
		packet.writeD(0x01);
		return true;
	}
}
