package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.RecipeTemplate;

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
		writeD(_isDwarvenCraft ? 0x00 : 0x01);
		writeD(_currentMp);

		writeD(_recipes.size());

		for(RecipeTemplate recipe : _recipes)
		{
			writeD(recipe.getId());
			writeD(1); //??
		}
	}
}