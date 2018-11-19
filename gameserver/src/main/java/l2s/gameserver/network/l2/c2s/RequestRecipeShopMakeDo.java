package l2s.gameserver.network.l2.c2s;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.ManufactureItem;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.RecipeShopItemInfoPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.templates.item.RecipeTemplate;
import l2s.gameserver.templates.item.data.ChancedItemData;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.TradeHelper;

public class RequestRecipeShopMakeDo extends L2GameClientPacket
{
	private int _manufacturerId;
	private int _recipeId;
	private long _price;

	@Override
	protected void readImpl()
	{
		_manufacturerId = readD();
		_recipeId = readD();
		_price = readQ();
	}

	@Override
	protected void runImpl()
	{
		Player buyer = getClient().getActiveChar();
		if(buyer == null)
			return;

		if(buyer.isActionsDisabled())
		{
			buyer.sendActionFailed();
			return;
		}

		if(buyer.isInStoreMode())
		{
			buyer.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(buyer.isInTrade())
		{
			buyer.sendActionFailed();
			return;
		}

		if(buyer.isFishing())
		{
			buyer.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		if(buyer.isInTrainingCamp())
		{
			buyer.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		if(!buyer.getPlayerAccess().UseTrade)
		{
			buyer.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
			return;
		}

		Player manufacturer = (Player) buyer.getVisibleObject(_manufacturerId);
		if(manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.checkInteractionDistance(buyer))
		{
			buyer.sendActionFailed();
			return;
		}

		RecipeTemplate recipe = null;
		for(ManufactureItem mi : manufacturer.getCreateList())
			if(mi.getRecipeId() == _recipeId)
				if(_price == mi.getCost())
				{
					recipe = RecipeHolder.getInstance().getRecipeByRecipeId(_recipeId);
					break;
				}

		if(recipe == null)
		{
			buyer.sendActionFailed();
			return;
		}

		if(recipe.getMaterials().length == 0 || recipe.getProducts().length == 0)
		{
			manufacturer.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
			buyer.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
			return;
		}

		if(recipe.getLevel() > manufacturer.getSkillLevel(!recipe.isCommon() ? 172 : 1320))
		{
			buyer.sendActionFailed();
			return;
		}

		if(!manufacturer.findRecipe(_recipeId))
		{
			buyer.sendActionFailed();
			return;
		}

		int success = 0;
		if(manufacturer.getCurrentMp() < recipe.getMpConsume())
		{
			manufacturer.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			buyer.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeShopItemInfoPacket(buyer, manufacturer, _recipeId, _price, success));
			return;
		}

		buyer.getInventory().writeLock();
		try
		{
			if(buyer.getAdena() < _price)
			{
				buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfoPacket(buyer, manufacturer, _recipeId, _price, success));
				return;
			}

			ItemData[] materials = recipe.getMaterials();

			for(ItemData material : materials)
			{
				if(material.getCount() == 0)
					continue;

				ItemInstance item = buyer.getInventory().getItemByItemId(material.getId());
				if(item == null || material.getCount() > item.getCount())
				{
					buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeShopItemInfoPacket(buyer, manufacturer, _recipeId, _price, success));
					return;
				}
			}

			if(!buyer.reduceAdena(_price, false))
			{
				buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfoPacket(buyer, manufacturer, _recipeId, _price, success));
				return;
			}

			for(ItemData material : materials)
			{
				if(material.getCount() == 0)
					continue;

				buyer.getInventory().destroyItemByItemId(material.getId(), material.getCount());
				//TODO audit
				buyer.sendPacket(SystemMessagePacket.removeItems(material.getId(), material.getCount()));
			}

			long tax = TradeHelper.getTax(manufacturer, _price);
			if(tax > 0)
				_price -= tax;

			manufacturer.addAdena(_price);
		}
		finally
		{
			buyer.getInventory().writeUnlock();
		}

		manufacturer.reduceCurrentMp(recipe.getMpConsume(), null);
		manufacturer.sendStatusUpdate(false, false, StatusUpdatePacket.CUR_MP);

		ChancedItemData product = recipe.getRandomProduct();
		if(product != null)
		{
			int itemId = product.getId();
			long itemsCount = product.getCount();
			int rate = recipe.getSuccessRate();
			rate += buyer.getPremiumAccount().getBonus().getCraftChance();
			rate = Math.min(100, rate);

			if(Rnd.chance(rate))
			{
				ItemFunctions.addItem(buyer, itemId, itemsCount, true);

				if(itemsCount > 1)
				{
					SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_CREATED_S2_S3_AT_THE_PRICE_OF_S4_ADENA);
					sm.addName(manufacturer);
					sm.addItemName(itemId);
					sm.addLong(itemsCount);
					sm.addLong(_price);
					buyer.sendPacket(sm);

					sm = new SystemMessagePacket(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA);
					sm.addName(buyer);
					sm.addItemName(itemId);
					sm.addLong(itemsCount);
					sm.addLong(_price);
					manufacturer.sendPacket(sm);

				}
				else
				{
					SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
					sm.addName(manufacturer);
					sm.addItemName(itemId);
					sm.addLong(_price);
					buyer.sendPacket(sm);

					sm = new SystemMessagePacket(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA);
					sm.addName(buyer);
					sm.addItemName(itemId);
					sm.addLong(_price);
					manufacturer.sendPacket(sm);
				}
				success = 1;
			}
			else
			{
				SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA);
				sm.addName(manufacturer);
				sm.addItemName(itemId);
				sm.addLong(_price);
				buyer.sendPacket(sm);

				sm = new SystemMessagePacket(SystemMsg.YOUR_ATTEMPT_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA_HAS_FAILED);
				sm.addName(buyer);
				sm.addItemName(itemId);
				sm.addLong(_price);
				manufacturer.sendPacket(sm);
			}
		}
	    else
		{
			SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA);
			sm.addName(manufacturer);
			sm.addItemName(recipe.getProducts()[0].getId());
			sm.addLong(_price);
			buyer.sendPacket(sm);

			sm = new SystemMessagePacket(SystemMsg.YOUR_ATTEMPT_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA_HAS_FAILED);
			sm.addName(buyer);
			sm.addItemName(recipe.getProducts()[0].getId());
			sm.addLong(_price);
			manufacturer.sendPacket(sm);
		}

		buyer.sendChanges();
		buyer.sendPacket(new RecipeShopItemInfoPacket(buyer, manufacturer, _recipeId, _price, success));
	}
}