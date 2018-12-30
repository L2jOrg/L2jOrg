package org.l2j.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.item.RecipeTemplate;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

public final class RecipeHolder extends AbstractHolder
{
	private static final RecipeHolder _instance = new RecipeHolder();

	private final HashIntObjectMap<RecipeTemplate> _listByRecipeId = new HashIntObjectMap<RecipeTemplate>();
	private final HashIntObjectMap<RecipeTemplate> _listByRecipeItem = new HashIntObjectMap<RecipeTemplate>();

	public static RecipeHolder getInstance()
	{
		return _instance;
	}

	public void addRecipe(RecipeTemplate recipe)
	{
		_listByRecipeId.put(recipe.getId(), recipe);
		_listByRecipeItem.put(recipe.getItemId(), recipe);
	}

	public RecipeTemplate getRecipeByRecipeId(int id)
	{
		return _listByRecipeId.get(id);
	}

	public RecipeTemplate getRecipeByRecipeItem(int id)
	{
		return _listByRecipeItem.get(id);
	}

	public Collection<RecipeTemplate> getRecipes()
	{
		Collection<RecipeTemplate> result = new ArrayList<RecipeTemplate>(size());
		for(int key : _listByRecipeId.keySet().toArray())
		{
			result.add(_listByRecipeId.get(key));
		}
		return result;
	}

	@Override
	public int size()
	{
		return _listByRecipeId.size();
	}

	@Override
	public void clear()
	{
		_listByRecipeId.clear();
		_listByRecipeItem.clear();
	}
}