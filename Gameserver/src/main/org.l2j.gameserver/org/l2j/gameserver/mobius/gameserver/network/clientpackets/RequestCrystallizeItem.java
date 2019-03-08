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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketReader;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.enums.Race;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.mobius.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.util.List;

/**
 * This class ...
 * @version $Revision: 1.2.2.3.2.5 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestCrystallizeItem extends IClientIncomingPacket
{
	private int _objectId;
	private long _count;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_objectId = packet.getInt();
		_count = packet.getLong();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		
		if (activeChar == null)
		{
			LOGGER.finer("RequestCrystalizeItem: activeChar was null");
			return;
		}
		
		// if (!client.getFloodProtectors().getTransaction().tryPerformAction("crystallize"))
		// {
		// activeChar.sendMessage("You are crystallizing too fast.");
		// return;
		// }
		
		if (_count <= 0)
		{
			Util.handleIllegalPlayerAction(activeChar, "[RequestCrystallizeItem] count <= 0! ban! oid: " + _objectId + " owner: " + activeChar.getName(), Config.DEFAULT_PUNISH);
			return;
		}
		
		if ((activeChar.getPrivateStoreType() != PrivateStoreType.NONE) || !activeChar.isInCrystallize())
		{
			client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}
		
		final int skillLevel = activeChar.getSkillLevel(CommonSkill.CRYSTALLIZE.getId());
		if (skillLevel <= 0)
		{
			client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			if ((activeChar.getRace() != Race.DWARF) && (activeChar.getClassId().ordinal() != 117) && (activeChar.getClassId().ordinal() != 55))
			{
				LOGGER.info("Player " + activeChar + " used crystalize with classid: " + activeChar.getClassId().ordinal());
			}
			return;
		}
		
		final PcInventory inventory = activeChar.getInventory();
		if (inventory != null)
		{
			final L2ItemInstance item = inventory.getItemByObjectId(_objectId);
			if (item == null)
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (item.isHeroItem())
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (_count > item.getCount())
			{
				_count = activeChar.getInventory().getItemByObjectId(_objectId).getCount();
			}
		}
		
		final L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);
		if ((itemToRemove == null) || itemToRemove.isShadowItem() || itemToRemove.isTimeLimitedItem())
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!itemToRemove.getItem().isCrystallizable() || (itemToRemove.getItem().getCrystalCount() <= 0) || (itemToRemove.getItem().getCrystalType() == CrystalType.NONE))
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}
		
		if (!activeChar.getInventory().canManipulateWithItemId(itemToRemove.getId()))
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_CRYSTALLIZED);
			return;
		}
		
		// Check if the char can crystallize items and return if false;
		boolean canCrystallize = true;
		
		switch (itemToRemove.getItem().getCrystalTypePlus())
		{
			case D:
			{
				if (skillLevel < 1)
				{
					canCrystallize = false;
				}
				break;
			}
			case C:
			{
				if (skillLevel < 2)
				{
					canCrystallize = false;
				}
				break;
			}
			case B:
			{
				if (skillLevel < 3)
				{
					canCrystallize = false;
				}
				break;
			}
			case A:
			{
				if (skillLevel < 4)
				{
					canCrystallize = false;
				}
				break;
			}
			case S:
			{
				if (skillLevel < 5)
				{
					canCrystallize = false;
				}
				break;
			}
			case R:
			{
				if (skillLevel < 6)
				{
					canCrystallize = false;
				}
				break;
			}
		}
		
		if (!canCrystallize)
		{
			client.sendPacket(SystemMessageId.YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final List<ItemChanceHolder> crystallizationRewards = ItemCrystallizationData.getInstance().getCrystallizationRewards(itemToRemove);
		if ((crystallizationRewards == null) || crystallizationRewards.isEmpty())
		{
			activeChar.sendPacket(SystemMessageId.CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED);
			return;
		}
		
		// activeChar.setInCrystallize(true);
		
		// unequip if needed
		SystemMessage sm;
		if (itemToRemove.isEquipped())
		{
			final L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot());
			final InventoryUpdate iu = new InventoryUpdate();
			for (L2ItemInstance item : unequiped)
			{
				iu.addModifiedItem(item);
			}
			activeChar.sendInventoryUpdate(iu);
			
			if (itemToRemove.getEnchantLevel() > 0)
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
				sm.addInt(itemToRemove.getEnchantLevel());
				sm.addItemName(itemToRemove);
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
				sm.addItemName(itemToRemove);
			}
			client.sendPacket(sm);
		}
		
		// remove from inventory
		final L2ItemInstance removedItem = activeChar.getInventory().destroyItem("Crystalize", _objectId, _count, activeChar, null);
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addRemovedItem(removedItem);
		activeChar.sendInventoryUpdate(iu);
		
		for (ItemChanceHolder holder : crystallizationRewards)
		{
			final double rand = Rnd.nextDouble() * 100;
			if (rand < holder.getChance())
			{
				// add crystals
				final L2ItemInstance createdItem = activeChar.getInventory().addItem("Crystalize", holder.getId(), holder.getCount(), activeChar, activeChar);
				
				sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
				sm.addItemName(createdItem);
				sm.addLong(holder.getCount());
				client.sendPacket(sm);
			}
		}
		
		sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_CRYSTALLIZED);
		sm.addItemName(removedItem);
		client.sendPacket(sm);
		
		activeChar.broadcastUserInfo();
		
		activeChar.setInCrystallize(false);
	}
}
