package l2s.gameserver.handler.items.impl;

import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.RecipeBookItemListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.RecipeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeItemHandler extends DefaultItemHandler
{
	protected static final Logger _log = LoggerFactory.getLogger(RecipeItemHandler.class);

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		RecipeTemplate rp = RecipeHolder.getInstance().getRecipeByRecipeItem(item.getItemId());
		if(rp == null)
		{
			_log.warn("RecipeTemplate: Recipe " + item.getItemId() + " is not working or not a recipe.");
			return false;
		}

		if(!rp.isCommon())
		{
			if(player.getDwarvenRecipeLimit() > 0)
			{
				if(player.getDwarvenRecipeBook().size() >= player.getDwarvenRecipeLimit())
				{
					player.sendPacket(SystemMsg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
					return false;
				}

				if(rp.getLevel() > player.getSkillLevel(Skill.SKILL_CRAFTING))
				{
					player.sendPacket(SystemMsg.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
					return false;
				}

				if(player.hasRecipe(rp))
				{
					player.sendPacket(SystemMsg.THAT_RECIPE_IS_ALREADY_REGISTERED);
					return false;
				}

				if(!player.getInventory().destroyItem(item, 1L))
				{
					player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
					return false;
				}
				// add recipe to recipebook
				player.registerRecipe(rp, true);
				player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
				player.sendPacket(new RecipeBookItemListPacket(player, true));
				return true;
			}
			else
				player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
		}
		else if(player.getCommonRecipeLimit() > 0)
		{
			if(player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit())
			{
				player.sendPacket(SystemMsg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
				return false;
			}

			if(rp.getLevel() > player.getSkillLevel(Skill.SKILL_COMMON_CRAFTING))
			{
				player.sendPacket(SystemMsg.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
				return false;
			}

			if(player.hasRecipe(rp))
			{
				player.sendPacket(SystemMsg.THAT_RECIPE_IS_ALREADY_REGISTERED);
				return false;
			}

			if(!player.getInventory().destroyItem(item, 1L))
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
				return false;
			}
			player.registerRecipe(rp, true);
			player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
			player.sendPacket(new RecipeBookItemListPacket(player, false));
			return true;
		}
		else
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
		return false;
	}
}