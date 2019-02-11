package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.RecipeHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.RecipeItemMakeInfoPacket;
import org.l2j.gameserver.templates.item.RecipeTemplate;

import java.nio.ByteBuffer;

public class RequestRecipeItemMakeInfo extends L2GameClientPacket
{
	private int _id;

	/**
	 * packet type id 0xB7
	 * format:		cd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_id = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
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