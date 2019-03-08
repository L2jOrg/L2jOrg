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
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Map;

/**
 ** @author Gnacik
 */
public class ShopPreviewInfo implements IClientOutgoingPacket
{
	private final Map<Integer, Integer> _itemlist;
	
	public ShopPreviewInfo(Map<Integer, Integer> itemlist)
	{
		_itemlist = itemlist;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SHOP_PREVIEW_INFO.writeId(packet);
		
		packet.writeD(Inventory.PAPERDOLL_TOTALSLOTS);
		// Slots
		packet.writeD(getFromList(Inventory.PAPERDOLL_UNDER));
		packet.writeD(getFromList(Inventory.PAPERDOLL_REAR));
		packet.writeD(getFromList(Inventory.PAPERDOLL_LEAR));
		packet.writeD(getFromList(Inventory.PAPERDOLL_NECK));
		packet.writeD(getFromList(Inventory.PAPERDOLL_RFINGER));
		packet.writeD(getFromList(Inventory.PAPERDOLL_LFINGER));
		packet.writeD(getFromList(Inventory.PAPERDOLL_HEAD));
		packet.writeD(getFromList(Inventory.PAPERDOLL_RHAND));
		packet.writeD(getFromList(Inventory.PAPERDOLL_LHAND));
		packet.writeD(getFromList(Inventory.PAPERDOLL_GLOVES));
		packet.writeD(getFromList(Inventory.PAPERDOLL_CHEST));
		packet.writeD(getFromList(Inventory.PAPERDOLL_LEGS));
		packet.writeD(getFromList(Inventory.PAPERDOLL_FEET));
		packet.writeD(getFromList(Inventory.PAPERDOLL_CLOAK));
		packet.writeD(getFromList(Inventory.PAPERDOLL_RHAND));
		packet.writeD(getFromList(Inventory.PAPERDOLL_HAIR));
		packet.writeD(getFromList(Inventory.PAPERDOLL_HAIR2));
		packet.writeD(getFromList(Inventory.PAPERDOLL_RBRACELET));
		packet.writeD(getFromList(Inventory.PAPERDOLL_LBRACELET));
		return true;
	}
	
	private int getFromList(int key)
	{
		return (_itemlist.containsKey(key) ? _itemlist.get(key) : 0);
	}
}