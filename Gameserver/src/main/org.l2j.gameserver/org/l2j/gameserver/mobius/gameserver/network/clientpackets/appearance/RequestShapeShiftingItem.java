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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.appearance;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.data.xml.impl.AppearanceItemData;
import com.l2jmobius.gameserver.enums.InventorySlot;
import com.l2jmobius.gameserver.enums.ItemLocation;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.actor.request.ShapeShiftingItemRequest;
import com.l2jmobius.gameserver.model.holders.AppearanceHolder;
import com.l2jmobius.gameserver.model.itemcontainer.PcInventory;
import com.l2jmobius.gameserver.model.items.L2Item;
import com.l2jmobius.gameserver.model.items.appearance.AppearanceStone;
import com.l2jmobius.gameserver.model.items.appearance.AppearanceType;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.variables.ItemVariables;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.ExAdenaInvenCount;
import com.l2jmobius.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import com.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmobius.gameserver.network.serverpackets.appearance.ExShapeShiftingResult;

/**
 * @author UnAfraid
 */
public class RequestShapeShiftingItem implements IClientIncomingPacket
{
	private int _targetItemObjId;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_targetItemObjId = packet.readD();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final ShapeShiftingItemRequest request = player.getRequest(ShapeShiftingItemRequest.class);
		
		if (player.isInStoreMode() || player.isCrafting() || player.isProcessingRequest() || player.isProcessingTransaction() || (request == null))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
			return;
		}
		
		final PcInventory inventory = player.getInventory();
		final L2ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		L2ItemInstance stone = request.getAppearanceStone();
		
		if ((targetItem == null) || (stone == null))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone.getOwnerId() != player.getObjectId()) || (targetItem.getOwnerId() != player.getObjectId()))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!targetItem.getItem().isAppearanceable())
		{
			client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_MODIFIED_OR_RESTORED);
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((targetItem.getItemLocation() != ItemLocation.INVENTORY) && (targetItem.getItemLocation() != ItemLocation.PAPERDOLL))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if ((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final AppearanceStone appearanceStone = AppearanceItemData.getInstance().getStone(stone.getId());
		if (appearanceStone == null)
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (!appearanceStone.checkConditions(player, targetItem))
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		final L2ItemInstance extractItem = request.getAppearanceExtractItem();
		
		int extracItemId = 0;
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getType() != AppearanceType.FIXED))
		{
			if (extractItem == null)
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getOwnerId() != player.getObjectId())
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (!extractItem.getItem().isAppearanceable())
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItemLocation() != ItemLocation.INVENTORY) && (extractItem.getItemLocation() != ItemLocation.PAPERDOLL))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getItem().getCrystalType().isGreater(targetItem.getItem().getCrystalType()))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if (extractItem.getVisualId() > 0)
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItemType() != targetItem.getItemType()) || (extractItem.getId() == targetItem.getId()) || (extractItem.getObjectId() == targetItem.getObjectId()))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			if ((extractItem.getItem().getBodyPart() != targetItem.getItem().getBodyPart()) && ((extractItem.getItem().getBodyPart() != L2Item.SLOT_FULL_ARMOR) || (targetItem.getItem().getBodyPart() != L2Item.SLOT_CHEST)))
			{
				client.sendPacket(ExShapeShiftingResult.CLOSE);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
			
			extracItemId = extractItem.getId();
		}
		
		long cost = appearanceStone.getCost();
		if (cost > player.getAdena())
		{
			client.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		
		if (stone.getCount() < 1L)
		{
			client.sendPacket(ExShapeShiftingResult.CLOSE);
			player.removeRequest(ShapeShiftingItemRequest.class);
			return;
		}
		if (appearanceStone.getType() == AppearanceType.NORMAL)
		{
			if (inventory.destroyItem(getClass().getSimpleName(), extractItem, 1, player, this) == null)
			{
				client.sendPacket(ExShapeShiftingResult.FAILED);
				player.removeRequest(ShapeShiftingItemRequest.class);
				return;
			}
		}
		
		inventory.destroyItem(getClass().getSimpleName(), stone, 1, player, this);
		player.reduceAdena(getClass().getSimpleName(), cost, extractItem, false);
		
		switch (appearanceStone.getType())
		{
			case RESTORE:
			{
				targetItem.setVisualId(0);
				targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, 0);
				break;
			}
			case NORMAL:
			{
				targetItem.setVisualId(extractItem.getId());
				break;
			}
			case BLESSED:
			{
				targetItem.setVisualId(extractItem.getId());
				break;
			}
			case FIXED:
			{
				if (appearanceStone.getVisualIds().isEmpty())
				{
					extracItemId = appearanceStone.getVisualId();
					targetItem.setVisualId(appearanceStone.getVisualId());
					targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
				}
				else
				{
					final AppearanceHolder holder = appearanceStone.findVisualChange(targetItem);
					if (holder != null)
					{
						extracItemId = holder.getVisualId();
						targetItem.setVisualId(holder.getVisualId());
						targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_STONE_ID, appearanceStone.getId());
					}
				}
				break;
			}
		}
		
		if ((appearanceStone.getType() != AppearanceType.RESTORE) && (appearanceStone.getLifeTime() > 0))
		{
			targetItem.getVariables().set(ItemVariables.VISUAL_APPEARANCE_LIFE_TIME, System.currentTimeMillis() + appearanceStone.getLifeTime());
			targetItem.scheduleVisualLifeTime();
		}
		
		targetItem.getVariables().storeMe();
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(targetItem);
		if (extractItem != null)
		{
			iu.addModifiedItem(extractItem);
		}
		if (inventory.getItemByObjectId(stone.getObjectId()) == null)
		{
			iu.addRemovedItem(stone);
		}
		else
		{
			iu.addModifiedItem(stone);
		}
		player.sendInventoryUpdate(iu);
		
		player.removeRequest(ShapeShiftingItemRequest.class);
		client.sendPacket(new ExShapeShiftingResult(ExShapeShiftingResult.RESULT_SUCCESS, targetItem.getId(), extracItemId));
		if (targetItem.isEquipped())
		{
			player.broadcastUserInfo();
			final ExUserInfoEquipSlot slots = new ExUserInfoEquipSlot(player, false);
			for (InventorySlot slot : InventorySlot.values())
			{
				if (slot.getSlot() == targetItem.getLocationSlot())
				{
					slots.addComponentType(slot);
				}
			}
			client.sendPacket(slots);
		}
		client.sendPacket(new ExAdenaInvenCount(player));
	}
}
