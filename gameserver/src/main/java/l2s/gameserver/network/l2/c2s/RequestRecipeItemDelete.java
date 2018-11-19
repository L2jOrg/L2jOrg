package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.RecipeBookItemListPacket;
import l2s.gameserver.templates.item.RecipeTemplate;

public class RequestRecipeItemDelete extends L2GameClientPacket
{
	private int _recipeId;

	@Override
	protected void readImpl()
	{
		_recipeId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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