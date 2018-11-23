package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.RecipeHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.RecipeItemMakeInfoPacket;
import org.l2j.gameserver.templates.item.RecipeTemplate;

public class RequestRecipeItemMakeInfo extends L2GameClientPacket
{
	private int _id;

	/**
	 * packet type id 0xB7
	 * format:		cd
	 */
	@Override
	protected void readImpl()
	{
		_id = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(_id);
		if(recipe == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		sendPacket(new RecipeItemMakeInfoPacket(activeChar, recipe, 0xffffffff));
	}
}