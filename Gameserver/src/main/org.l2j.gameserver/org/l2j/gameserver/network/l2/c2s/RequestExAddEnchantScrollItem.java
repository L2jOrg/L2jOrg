package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.EnchantItemHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExPutEnchantScrollItemResult;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.support.EnchantScroll;
import org.l2j.gameserver.utils.Log;

/**
 * @author Bonux
**/
public class RequestExAddEnchantScrollItem extends L2GameClientPacket
{
	private int _scrollObjectId;
	private int _itemObjectId;

	@Override
	protected void readImpl()
	{
		_scrollObjectId = readInt();
		_itemObjectId = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.isActionsDisabled() || player.isInStoreMode() || player.isInTrade())
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			return;
		}

		PcInventory inventory = player.getInventory();
		ItemInstance itemToEnchant = inventory.getItemByObjectId(_itemObjectId);
		ItemInstance scroll = inventory.getItemByObjectId(_scrollObjectId);

		if(itemToEnchant == null || scroll == null)
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			return;
		}

		Log.add(player.getName() + "|Trying to put enchant|" + itemToEnchant.getItemId() + "|+" + itemToEnchant.getEnchantLevel() + "|" + itemToEnchant.getObjectId(), "enchants");

		int scrollId = scroll.getItemId();
		int itemId = itemToEnchant.getItemId();

		EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scrollId);

		if(enchantScroll == null || !enchantScroll.getItems().contains(itemId) && !itemToEnchant.canBeEnchanted() || itemToEnchant.isStackable())
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			return;
		}

		if(itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			return;
		}

		if(player.isInStoreMode())
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return;
		}

		if((scroll = inventory.getItemByObjectId(scroll.getObjectId())) == null)
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			return;
		}

		if(enchantScroll == null)
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			return;
		}

		if(enchantScroll.getItems().size() > 0)
		{
			if(!enchantScroll.getItems().contains(itemId))
			{
				player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
				player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
				return;
			}
		}
		else
		{
			if(!enchantScroll.containsGrade(itemToEnchant.getGrade()))
			{
				player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
				player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
				return;
			}

			int itemType = itemToEnchant.getTemplate().getType2();
			switch(enchantScroll.getType())
			{
				case ARMOR:
					if(itemType == ItemTemplate.TYPE2_WEAPON)
					{
						player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
						player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
						return;
					}
					break;
				case WEAPON:
					if(itemType == ItemTemplate.TYPE2_SHIELD_ARMOR || itemType == ItemTemplate.TYPE2_ACCESSORY)
					{
						player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
						player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
						return;
					}
					break;
			}
		}

		if(enchantScroll.getMaxEnchant() != -1 && itemToEnchant.getEnchantLevel() >= enchantScroll.getMaxEnchant())
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			return;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(itemToEnchant.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExPutEnchantScrollItemResult.FAIL);
			return;
		}

		player.sendPacket(new ExPutEnchantScrollItemResult(scroll.getObjectId()));
		player.setEnchantScroll(scroll);
	}
}