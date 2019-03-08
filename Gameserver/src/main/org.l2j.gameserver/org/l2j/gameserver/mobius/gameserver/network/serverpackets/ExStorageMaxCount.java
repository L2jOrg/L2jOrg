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

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.stats.Stats;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author -Wooden-, KenM
 */
public class ExStorageMaxCount implements IClientOutgoingPacket
{
	private final int _inventory;
	private final int _warehouse;
	// private final int _freight; // Removed with 152.
	private final int _clan;
	private final int _privateSell;
	private final int _privateBuy;
	private final int _receipeD;
	private final int _recipe;
	private final int _inventoryExtraSlots;
	private final int _inventoryQuestItems;
	
	public ExStorageMaxCount(L2PcInstance activeChar)
	{
		_inventory = activeChar.getInventoryLimit();
		_warehouse = activeChar.getWareHouseLimit();
		// _freight = Config.ALT_FREIGHT_SLOTS; // Removed with 152.
		_privateSell = activeChar.getPrivateSellStoreLimit();
		_privateBuy = activeChar.getPrivateBuyStoreLimit();
		_clan = Config.WAREHOUSE_SLOTS_CLAN;
		_receipeD = activeChar.getDwarfRecipeLimit();
		_recipe = activeChar.getCommonRecipeLimit();
		_inventoryExtraSlots = (int) activeChar.getStat().getValue(Stats.INVENTORY_NORMAL, 0);
		_inventoryQuestItems = Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_STORAGE_MAX_COUNT.writeId(packet);
		
		packet.writeD(_inventory);
		packet.writeD(_warehouse);
		// packet.writeD(_freight); // Removed with 152.
		packet.writeD(_clan);
		packet.writeD(_privateSell);
		packet.writeD(_privateBuy);
		packet.writeD(_receipeD);
		packet.writeD(_recipe);
		packet.writeD(_inventoryExtraSlots); // Belt inventory slots increase count
		packet.writeD(_inventoryQuestItems);
		packet.writeD(40); // TODO: Find me!
		packet.writeD(40); // TODO: Find me!
		packet.writeD(0x64); // Artifact slots (Fixed)
		return true;
	}
}
