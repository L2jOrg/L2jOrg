package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_isDwarvenCraft ? 0x00 : 0x01);
		buffer.putInt(_currentMp);

		buffer.putInt(_recipes.size());

		for(RecipeTemplate recipe : _recipes)
		{
			buffer.putInt(recipe.getId());
			buffer.putInt(1); //??
		}
	}
}