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
package handlers.effecthandlers;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Disarm by inventory slot effect implementation. At end of effect, it re-equips that item.
 * @author Nik
 */
public final class Disarmor extends AbstractEffect
{
	private final Map<Integer, Integer> _unequippedItems; // PlayerObjId, ItemObjId
	private final long _slot;
	
	public Disarmor(StatsSet params)
	{
		_unequippedItems = new ConcurrentHashMap<>();
		
		final String slot = params.getString("slot", "chest");
		_slot = ItemTable.SLOTS.getOrDefault(slot, (long) ItemTemplate.SLOT_NONE);
		if (_slot == ItemTemplate.SLOT_NONE)
		{
			LOGGER.error("Unknown bodypart slot for effect: " + slot);
		}
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return (_slot != ItemTemplate.SLOT_NONE) && isPlayer(effected);
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!isPlayer(effected))
		{
			return;
		}
		
		final Player player = effected.getActingPlayer();
		final Item[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(_slot);
		if (unequiped.length > 0)
		{
			final InventoryUpdate iu = new InventoryUpdate();
			for (Item itm : unequiped)
			{
				iu.addModifiedItem(itm);
			}
			player.sendInventoryUpdate(iu);
			player.broadcastUserInfo();
			
			SystemMessage sm = null;
			if (unequiped[0].getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_HAS_BEEN_UNEQUIPPED);
				sm.addInt(unequiped[0].getEnchantLevel());
				sm.addItemName(unequiped[0]);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(unequiped[0]);
			}
			player.sendPacket(sm);
			effected.getInventory().blockItemSlot(_slot);
			_unequippedItems.put(effected.getObjectId(), unequiped[0].getObjectId());
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!isPlayer(effected))
		{
			return;
		}
		
		final Integer disarmedObjId = _unequippedItems.remove(effected.getObjectId());
		if ((disarmedObjId != null) && (disarmedObjId > 0))
		{
			final Player player = effected.getActingPlayer();
			player.getInventory().unblockItemSlot(_slot);
			
			final Item item = player.getInventory().getItemByObjectId(disarmedObjId);
			if (item != null)
			{
				player.getInventory().equipItem(item);
				final InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(item);
				player.sendInventoryUpdate(iu);
				
				SystemMessage sm = null;
				if (item.isEquipped())
				{
					if (item.getEnchantLevel() > 0)
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2);
						sm.addInt(item.getEnchantLevel());
						sm.addItemName(item);
					}
					else
					{
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
						sm.addItemName(item);
					}
					player.sendPacket(sm);
				}
			}
		}
	}
}
