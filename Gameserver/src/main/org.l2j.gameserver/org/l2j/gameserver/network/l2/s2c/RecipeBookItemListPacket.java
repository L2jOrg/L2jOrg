package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.RecipeTemplate;

public class RecipeBookItemListPacket extends L2GameServerPacket
{
	private Collection<RecipeTemplate> _recipes;
	private final boolean _isDwarvenCraft;
	private final int _currentMp;

	public RecipeBookItemListPacket(Player player, boolean isDwarvenCraft)
	{
		_isDwarvenCraft = isDwarvenCraft;
		_currentMp = (int) player.getCurrentMp();
		if(isDwarvenCraft)
			_recipes = player.getDwarvenRecipeBook();
		else
			_recipes = player.getCommonRecipeBook();
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_isDwarvenCraft ? 0x00 : 0x01);
		writeInt(_currentMp);

		writeInt(_recipes.size());

		for(RecipeTemplate recipe : _recipes)
		{
			writeInt(recipe.getId());
			writeInt(1); //??
		}
	}
}