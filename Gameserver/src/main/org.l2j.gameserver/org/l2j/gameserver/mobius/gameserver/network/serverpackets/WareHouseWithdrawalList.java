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
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class WareHouseWithdrawalList extends AbstractItemPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 1;
	private final int _sendType;
	private L2PcInstance _activeChar;
	private long _playerAdena;
	private final int _invSize;
	private Collection<L2ItemInstance> _items;
	private final List<Integer> _itemsStackable = new ArrayList<>();
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private int _whType;
	
	public WareHouseWithdrawalList(int sendType, L2PcInstance player, int type)
	{
		_sendType = sendType;
		_activeChar = player;
		_whType = type;
		
		_playerAdena = _activeChar.getAdena();
		_invSize = player.getInventory().getSize();
		if (_activeChar.getActiveWarehouse() == null)
		{
			LOGGER.warning("error while sending withdraw request to: " + _activeChar.getName());
			return;
		}
		
		_items = _activeChar.getActiveWarehouse().getItems();
		
		for (L2ItemInstance item : _items)
		{
			if (item.isStackable())
			{
				_itemsStackable.add(item.getDisplayId());
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.WAREHOUSE_WITHDRAW_LIST.writeId(packet);
		packet.writeC(_sendType);
		if (_sendType == 2)
		{
			packet.writeH(0x00);
			packet.writeD(_invSize);
			packet.writeD(_items.size());
			for (L2ItemInstance item : _items)
			{
				writeItem(packet, item);
				packet.writeD(item.getObjectId());
				packet.writeD(0x00);
				packet.writeD(0x00);
			}
		}
		else
		{
			packet.writeH(_whType);
			packet.writeQ(_playerAdena);
			packet.writeD(_invSize);
			packet.writeD(_items.size());
		}
		return true;
	}
}
