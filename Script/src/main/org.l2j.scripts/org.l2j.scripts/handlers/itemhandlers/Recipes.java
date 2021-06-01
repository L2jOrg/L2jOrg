/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.settings.CharacterSettings;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author Zoey76
 */
public class Recipes implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {
		if (!CharacterSettings.craftEnabled()) {
			playable.sendMessage("Crafting is disabled, you cannot register this recipe.");
			return false;
		}

		if(!(playable instanceof Player player)) {
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}

		if (player.isCrafting()) {
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
			return false;
		}
		
		final RecipeList recipeList = RecipeData.getInstance().getRecipeByItemId(item.getId());
		if (isNull(recipeList)) {
			return false;
		}
		
		if (player.hasRecipeList(recipeList.getId())) {
			player.sendPacket(SystemMessageId.THAT_RECIPE_IS_ALREADY_REGISTERED);
			RecipeController.getInstance().requestBookOpen(player, true);
			return false;
		}
		
		boolean canCraft;
		boolean recipeLevel;
		boolean recipeLimit;

		if (recipeList.isDwarvenRecipe()) {
			canCraft = player.hasDwarvenCraft();
			recipeLevel = recipeList.getLevel() > player.getDwarvenCraft();
			recipeLimit = player.getDwarvenRecipeBook().length >= player.getDwarfRecipeLimit();
		} else {
			canCraft = player.hasCommonCraft();
			recipeLevel = recipeList.getLevel() > player.getCommonCraft();
			recipeLimit = player.getCommonRecipeBook().length >= player.getCommonRecipeLimit();
		}
		
		if (!canCraft) {
			player.sendPacket(SystemMessageId.THE_RECIPE_CANNOT_BE_REGISTERED_YOU_DO_NOT_HAVE_THE_ABILITY_TO_CREATE_ITEMS);
			return false;
		}
		
		if (recipeLevel) {
			player.sendPacket(SystemMessageId.YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
			return false;
		}
		
		if (recipeLimit) {
			player.sendPacket(getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_BE_REGISTERED)
					.addInt(recipeList.isDwarvenRecipe() ? player.getDwarfRecipeLimit() : player.getCommonRecipeLimit()));
			return false;
		}
		

		player.registerRecipe(recipeList);
		player.destroyItem("Consume", item.getObjectId(), 1, null, false);
		player.sendPacket( getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED).addItemName(item));
		RecipeController.getInstance().requestBookOpen(player, true);
		return true;
	}
}
