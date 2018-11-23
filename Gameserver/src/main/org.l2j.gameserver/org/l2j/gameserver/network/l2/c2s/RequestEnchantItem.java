package org.l2j.gameserver.network.l2.c2s;

import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.holder.EnchantItemHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.PcInventory;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.EnchantResultPacket;
import org.l2j.gameserver.network.l2.s2c.InventoryUpdatePacket;
import org.l2j.gameserver.network.l2.s2c.MagicSkillUse;
import org.l2j.gameserver.network.l2.s2c.SystemMessage;
import org.l2j.gameserver.templates.item.ItemGrade;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.support.EnchantScroll;
import org.l2j.gameserver.templates.item.support.EnchantVariation;
import org.l2j.gameserver.templates.item.support.EnchantVariation.EnchantLevel;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestEnchantItem extends L2GameClientPacket
{
	private static final int ENCHANT_DELAY = 1500;

	private static final Logger _log = LoggerFactory.getLogger(RequestEnchantItem.class);

	private static final int SUCCESS_VISUAL_EFF_ID = 5965;
	private static final int FAIL_VISUAL_EFF_ID = 5949;

	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		readInt();
	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.isActionsDisabled())
		{
			player.setEnchantScroll(null);
			player.sendActionFailed();
			return;
		}

		if(player.isInTrade())
		{
			player.setEnchantScroll(null);
			player.sendActionFailed();
			return;
		}

		if(System.currentTimeMillis() <= (player.getLastEnchantItemTime() + ENCHANT_DELAY))
		{
			player.setEnchantScroll(null);
			player.sendActionFailed();
			return;
		}

		if(player.isInStoreMode())
		{
			player.setEnchantScroll(null);
			player.sendPacket(EnchantResultPacket.CANCEL);
			player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendActionFailed();
			return;
		}

		final PcInventory inventory = player.getInventory();
		inventory.writeLock();
		try
		{
			final ItemInstance item = inventory.getItemByObjectId(_objectId);
			final ItemInstance scroll = player.getEnchantScroll();

			if(item == null || scroll == null)
			{
				player.sendActionFailed();
				return;
			}

			final EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scroll.getItemId());
			if(enchantScroll == null)
			{
				player.sendActionFailed();
				return;
			}

			if(enchantScroll.getMaxEnchant() != -1 && item.getEnchantLevel() >= enchantScroll.getMaxEnchant())
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.sendActionFailed();
				return;
			}

			if(enchantScroll.getItems().size() > 0)
			{
				if(!enchantScroll.getItems().contains(item.getItemId()))
				{
					player.sendPacket(EnchantResultPacket.CANCEL);
					player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
					player.sendActionFailed();
					return;
				}
			}
			else
			{
				if(!enchantScroll.containsGrade(item.getGrade()))
				{
					player.sendPacket(EnchantResultPacket.CANCEL);
					player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
					player.sendActionFailed();
					return;
				}

				final int itemType = item.getTemplate().getType2();
				switch(enchantScroll.getType())
				{
					case ARMOR:
						if(itemType == ItemTemplate.TYPE2_WEAPON || item.getTemplate().isHairAccessory())
						{
							player.sendPacket(EnchantResultPacket.CANCEL);
							player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
							player.sendActionFailed();
							return;
						}
						break;
					case WEAPON:
						if(itemType == ItemTemplate.TYPE2_SHIELD_ARMOR || itemType == ItemTemplate.TYPE2_ACCESSORY || item.getTemplate().isHairAccessory())
						{
							player.sendPacket(EnchantResultPacket.CANCEL);
							player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
							player.sendActionFailed();
							return;
						}
						break;
					case HAIR_ACCESSORY:
						if(!item.getTemplate().isHairAccessory())
						{
							player.sendPacket(EnchantResultPacket.CANCEL);
							player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
							player.sendActionFailed();
							return;
						}
						break;
				}
			}

			if(!enchantScroll.getItems().contains(item.getItemId()) && !item.canBeEnchanted())
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.sendActionFailed();
				return;
			}

			if(!inventory.destroyItem(scroll, 1L))
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendActionFailed();
				return;
			}

			final boolean equipped = item.isEquipped();
			if(equipped)
			{
				inventory.isRefresh = true;
				inventory.unEquipItem(item);
			}

			final EnchantVariation variation = EnchantItemHolder.getInstance().getEnchantVariation(enchantScroll.getVariationId());
			if(variation == null)
			{
				player.sendActionFailed();
				_log.warn("RequestEnchantItem: Cannot find variation ID[" + enchantScroll.getVariationId() + "] for enchant scroll ID[" + enchantScroll.getItemId() + "]!");
				return;
			}

			int newEnchantLvl = item.getEnchantLevel() + Rnd.get(enchantScroll.getMinEnchantStep(), enchantScroll.getMaxEnchantStep());
			newEnchantLvl = Math.min(newEnchantLvl, enchantScroll.getMaxEnchant());
			if(newEnchantLvl < item.getEnchantLevel()) //  А вдруг?
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendActionFailed();
				return;
			}

			final EnchantLevel enchantLevel = variation.getLevel(item.getEnchantLevel() + 1);
			if(enchantLevel == null)
			{
				player.sendActionFailed();
				_log.warn("RequestEnchantItem: Cannot find variation ID[" + enchantScroll.getVariationId() + "] enchant level[" + (item.getEnchantLevel() + 1) + "] for enchant scroll ID[" + enchantScroll.getItemId() + "]!");
				return;
			}

			double chance = enchantLevel.getBaseChance();
			if(item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
				chance = enchantLevel.getFullBodyChance();
			else if(item.getTemplate().isMagicWeapon())
				chance = enchantLevel.getMagicWeaponChance();

			chance += player.getPremiumAccount().getBonus().getEnchantChance();

			if(item.getGrade() != ItemGrade.NONE)
				chance *= player.getEnchantChanceModifier();

			chance = Math.min(100, chance);

			if(Rnd.chance(chance))
			{
				item.setEnchantLevel(newEnchantLvl);
				item.setJdbcState(JdbcEntityState.UPDATED);
				item.update();

				if(equipped)
				{
					inventory.equipItem(item);
					inventory.isRefresh = false;
				}

				player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, item));

				player.sendPacket(new EnchantResultPacket(0, 0, 0, item.getEnchantLevel()));

				if(enchantLevel.haveSuccessVisualEffect())
				{
					player.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_SUCCESSFULY_ENCHANTED_A__S2_S3).addName(player).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					player.broadcastPacket(new MagicSkillUse(player, player, SUCCESS_VISUAL_EFF_ID, 1, 500, 1500));
				}

				player.getListeners().onEnchantItem(item, true);
			}
			else
			{
				switch(enchantScroll.getResultType())
				{
					case CRYSTALS:
						if(item.isEquipped())
							player.sendDisarmMessage(item);

						Log.LogItem(player, Log.EnchantFail, item);

						if(!inventory.destroyItem(item, 1L))
						{
							player.sendActionFailed();
							return;
						}

						int crystalId = item.getGrade().getCrystalId();
						if(crystalId > 0 && item.getCrystalCountOnEchant() > 0 && !item.isFlagNoCrystallize())
						{
							int crystalAmount = item.getCrystalCountOnEchant();

							player.sendPacket(new EnchantResultPacket(1, crystalId, crystalAmount, 0));
							ItemFunctions.addItem(player, crystalId, crystalAmount, true);
						}
						else
							player.sendPacket(EnchantResultPacket.FAILED_NO_CRYSTALS);

						if(enchantScroll.showFailEffect())
							player.broadcastPacket(new MagicSkillUse(player, player, FAIL_VISUAL_EFF_ID, 1, 500, 1500));
						break;
					case DROP_ENCHANT:
						int enchantDropCount = enchantScroll.getEnchantDropCount();
						item.setEnchantLevel(Math.max(item.getEnchantLevel() - enchantDropCount, 0));
						item.setJdbcState(JdbcEntityState.UPDATED);
						item.update();

						if(equipped)
						{
							inventory.equipItem(item);
							inventory.isRefresh = false;
						}

						player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, item));
						player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
						player.sendPacket(EnchantResultPacket.BLESSED_FAILED);
						break;
					case NOTHING:
						player.sendPacket(EnchantResultPacket.ANCIENT_FAILED);
						break;
				}

				player.getListeners().onEnchantItem(item, false);
			}
		}
		finally
		{
			inventory.writeUnlock();

			player.updateStats();
		}

		player.setLastEnchantItemTime(System.currentTimeMillis());
	}
}