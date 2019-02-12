package org.l2j.gameserver.utils;

import org.l2j.commons.math.SafeMath;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.item.ItemTemplate;

import java.util.List;

public final class TradeHelper
{
	public static boolean checksIfCanOpenStore(Player player, int storeType)
	{
		if(player.getLevel() < Config.SERVICES_TRADE_MIN_LEVEL)
		{
			player.sendMessage(new CustomMessage("trade.NotHavePermission").addNumber(Config.SERVICES_TRADE_MIN_LEVEL));
			return false;
		}

		String tradeBan = player.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			player.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_BLOCKED_FROM_USING_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP);
			return false;
		}

		String BLOCK_ZONE = storeType == Player.STORE_PRIVATE_MANUFACTURE ? Zone.BLOCKED_ACTION_PRIVATE_WORKSHOP : Zone.BLOCKED_ACTION_PRIVATE_STORE;
		if(player.isActionBlocked(BLOCK_ZONE))
		{
			player.sendPacket(storeType == Player.STORE_PRIVATE_MANUFACTURE ? SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE : SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
			return false;
		}


		if(player.isCastingNow())
		{
			player.sendPacket(SystemMsg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
			return false;
		}

		if(player.isInCombat())
		{
			player.sendPacket(SystemMsg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return false;
		}

		if(player.isActionsDisabled() || player.isMounted() || player.isInOlympiadMode() || player.isInDuel() || player.isProcessingRequest())
			return false;

		if(Config.SERVICES_TRADE_ONLY_FAR)
		{
			boolean tradenear = false;
			for(Player p : World.getAroundPlayers(player, Config.SERVICES_TRADE_RADIUS, 200))
				if(p.isInStoreMode())
				{
					tradenear = true;
					break;
				}

			if(World.getAroundNpc(player, Config.SERVICES_TRADE_RADIUS + 100, 200).size() > 0)
				tradenear = true;

			if(tradenear)
			{
				player.sendMessage(new CustomMessage("trade.OtherTradersNear"));
				return false;
			}
		}

		return true;
	}

	/**
	 * Проверка торговца на валидность магазина
	 *
	 * @param player торговец
	 * @return true, если магазин валидный
	 */
	public static boolean validateStore(Player player)
	{
		return validateStore(player, 0);
	}

	/**
	 * Проверка торговца на валидность магазина
	 *
	 * @param player торговец
	 * @param adena зарезервированное количество адены, например цена за оффлайн торговлю
	 * @return true, если магазин валидный
	 */
	public static boolean validateStore(Player player, long adena)
	{
		if(player.isDead())
			return false;

		if(player.getLevel() < Config.SERVICES_TRADE_MIN_LEVEL)
			return false;

		String tradeBan = player.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			return false;

		String BLOCK_ZONE = player.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE ? Zone.BLOCKED_ACTION_PRIVATE_WORKSHOP : Zone.BLOCKED_ACTION_PRIVATE_STORE;
		if(player.isActionBlocked(BLOCK_ZONE))
			return false;

		switch (player.getPrivateStoreType())
		{
			case Player.STORE_PRIVATE_BUY:
				return validateBuyStore(player, adena);
			case Player.STORE_PRIVATE_SELL:
			case Player.STORE_PRIVATE_SELL_PACKAGE:
				return true; // TODO
			case Player.STORE_PRIVATE_MANUFACTURE:
				return true; // TODO
		}

		if(Config.SERVICES_TRADE_ONLY_FAR)
		{
			for(Creature c : World.getAroundCharacters(player, Config.SERVICES_TRADE_RADIUS, 200))
			{
				if(c.isNpc())
					return false;
				if(!c.isPlayer())
					continue;
				Player p = c.getPlayer();
				if(p.isInStoreMode())
					return false;
			}
		}

		return false;
	}

	public static boolean validateBuyStore(Player player, long adena)
	{
		List<TradeItem> buyList = player.getBuyList();
		if(buyList.isEmpty())
			return false;

		if(buyList.size() > player.getTradeLimit())
			return false;

		long totalCost = adena;
		int slots = 0;
		long weight = 0;

		try
		{
			ItemTemplate template;
			for (TradeItem item : buyList)
			{
				template = item.getItem();
				totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(item.getCount(), item.getOwnersPrice()));
				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), template.getWeight()));
				if(!template.isStackable() || player.getInventory().getItemByItemId(item.getItemId()) == null)
					slots++;
			}
		}
		catch (ArithmeticException ae)
		{
			return false;
		}

		if(totalCost > player.getAdena())
			return false;

		if(!player.getInventory().validateWeight(weight))
			return false;

		if(!player.getInventory().validateCapacity(slots))
			return false;

		return true;
	}

	public final static void purchaseItem(Player buyer, Player seller, TradeItem item)
	{
		long price = item.getCount() * item.getOwnersPrice();
		if(!item.getItem().isStackable())
		{
			if(item.getEnchantLevel() > 0)
			{
				seller.sendPacket(new SystemMessagePacket(SystemMsg.S2S3_HAS_BEEN_SOLD_TO_C1_AT_THE_PRICE_OF_S4_ADENA).addName(buyer).addInteger(item.getEnchantLevel()).addItemName(item.getItemId()).addLong(price));
				buyer.sendPacket(new SystemMessagePacket(SystemMsg.S2S3_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S4_ADENA).addName(seller).addInteger(item.getEnchantLevel()).addItemName(item.getItemId()).addLong(price));
			}
			else
			{
				seller.sendPacket(new SystemMessagePacket(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA).addName(buyer).addItemName(item.getItemId()).addLong(price));
				buyer.sendPacket(new SystemMessagePacket(SystemMsg.S2_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S3_ADENA).addName(seller).addItemName(item.getItemId()).addLong(price));
			}
		}
		else
		{
			seller.sendPacket(new SystemMessagePacket(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA).addName(buyer).addItemName(item.getItemId()).addLong(item.getCount()).addLong(price));
			buyer.sendPacket(new SystemMessagePacket(SystemMsg.S3_S2_HAS_BEEN_PURCHASED_FROM_C1_FOR_S4_ADENA).addName(seller).addItemName(item.getItemId()).addLong(item.getCount()).addLong(price));
		}
	}

	public final static long getTax(Player seller, long price)
	{
		long tax = (long) (price * Config.SERVICES_TRADE_TAX / 100);
		if(seller.isInZone(Zone.ZoneType.offshore))
			tax = (long) (price * Config.SERVICES_OFFSHORE_TRADE_TAX / 100);
		if(Config.SERVICES_PARNASSUS_NOTAX && seller.getReflection() == ReflectionManager.PARNASSUS)
			tax = 0;

		return tax;
	}

	/**
	 * Отключение режима торговли у персонажа, оф. трейдеров кикает.
	 */
	public static void cancelStore(Player activeChar)
	{
		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		activeChar.storePrivateStore();
		activeChar.broadcastCharInfo();
	}
}