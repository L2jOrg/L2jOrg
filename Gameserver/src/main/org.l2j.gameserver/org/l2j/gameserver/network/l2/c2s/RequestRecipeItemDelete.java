package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.RecipeHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.RecipeBookItemListPacket;
import org.l2j.gameserver.templates.item.RecipeTemplate;

import java.nio.ByteBuffer;

public class RequestRecipeItemDelete extends L2GameClientPacket
{
	private int _recipeId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_recipeId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE)
		{
			activeChar.sendActionFailed();
			return;
		}

		RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(_recipeId);
		if(recipe == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.unregisterRecipe(_recipeId);
		activeChar.sendPacket(new RecipeBookItemListPacket(activeChar, !recipe.isCommon()));
	}
}