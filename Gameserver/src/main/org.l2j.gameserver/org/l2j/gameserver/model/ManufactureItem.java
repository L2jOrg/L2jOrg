package org.l2j.gameserver.model;

import org.l2j.gameserver.data.xml.impl.RecipeData;

public class ManufactureItem {
    private final int _recipeId;
    private final long _cost;
    private final boolean _isDwarven;

    public ManufactureItem(int recipeId, long cost) {
        _recipeId = recipeId;
        _cost = cost;
        _isDwarven = RecipeData.getInstance().getRecipeList(_recipeId).isDwarvenRecipe();
    }

    public int getRecipeId() {
        return _recipeId;
    }

    public long getCost() {
        return _cost;
    }

    public boolean isDwarven() {
        return _isDwarven;
    }
}
