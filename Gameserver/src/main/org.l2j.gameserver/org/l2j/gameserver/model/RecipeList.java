/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.model;

/**
 * This class describes a Recipe used by Dwarf to craft Item. All RecipeList are made of Recipe (1 line of the recipe : Item-Quantity needed).
 */
public class RecipeList {
    /**
     * The Identifier of the Instance
     */
    private final int _id;
    /**
     * The crafting level needed to use this RecipeList
     */
    private final int _level;
    /**
     * The Identifier of the RecipeList
     */
    private final int _recipeId;
    /**
     * The name of the RecipeList
     */
    private final String _recipeName;
    /**
     * The crafting success rate when using the RecipeList
     */
    private final int _successRate;
    /**
     * The Identifier of the Item crafted with this RecipeList
     */
    private final int _itemId;
    /**
     * The quantity of Item crafted when using this RecipeList
     */
    private final int _count;
    /**
     * If this a common or a dwarven recipe
     */
    private final boolean _isDwarvenRecipe;
    /**
     * The table containing all Recipe (1 line of the recipe : Item-Quantity needed) of the RecipeList
     */
    private Recipe[] _recipes;
    /**
     * The table containing all RecipeStat for the statUse parameter of the RecipeList
     */
    private RecipeStat[] _statUse;
    /**
     * The table containing all RecipeStat for the altStatChange parameter of the RecipeList
     */
    private RecipeStat[] _altStatChange;
    /**
     * The Identifier of the Rare Item crafted with this RecipeList
     */
    private int _rareItemId;
    /**
     * The quantity of Rare Item crafted when using this RecipeList
     */
    private int _rareCount;
    /**
     * The chance of Rare Item crafted when using this RecipeList
     */
    private int _rarity;

    /**
     * Constructor of RecipeList (create a new Recipe).
     *
     * @param set
     * @param haveRare
     */
    public RecipeList(StatsSet set, boolean haveRare) {
        _recipes = new Recipe[0];
        _statUse = new RecipeStat[0];
        _altStatChange = new RecipeStat[0];
        _id = set.getInt("id");
        _level = set.getInt("craftLevel");
        _recipeId = set.getInt("recipeId");
        _recipeName = set.getString("recipeName");
        _successRate = set.getInt("successRate");
        _itemId = set.getInt("itemId");
        _count = set.getInt("count");
        if (haveRare) {
            _rareItemId = set.getInt("rareItemId");
            _rareCount = set.getInt("rareCount");
            _rarity = set.getInt("rarity");
        }
        _isDwarvenRecipe = set.getBoolean("isDwarvenRecipe");
    }

    /**
     * Add a Recipe to the RecipeList (add a line Item-Quantity needed to the Recipe).
     *
     * @param recipe
     */
    public void addRecipe(Recipe recipe) {
        final int len = _recipes.length;
        final Recipe[] tmp = new Recipe[len + 1];
        System.arraycopy(_recipes, 0, tmp, 0, len);
        tmp[len] = recipe;
        _recipes = tmp;
    }

    /**
     * Add a RecipeStat of the statUse parameter to the RecipeList.
     *
     * @param statUse
     */
    public void addStatUse(RecipeStat statUse) {
        final int len = _statUse.length;
        final RecipeStat[] tmp = new RecipeStat[len + 1];
        System.arraycopy(_statUse, 0, tmp, 0, len);
        tmp[len] = statUse;
        _statUse = tmp;
    }

    /**
     * Add a RecipeStat of the altStatChange parameter to the RecipeList.
     *
     * @param statChange
     */
    public void addAltStatChange(RecipeStat statChange) {
        final int len = _altStatChange.length;
        final RecipeStat[] tmp = new RecipeStat[len + 1];
        System.arraycopy(_altStatChange, 0, tmp, 0, len);
        tmp[len] = statChange;
        _altStatChange = tmp;
    }

    /**
     * @return the Identifier of the Instance.
     */
    public int getId() {
        return _id;
    }

    /**
     * @return the crafting level needed to use this RecipeList.
     */
    public int getLevel() {
        return _level;
    }

    /**
     * @return the Identifier of the RecipeList.
     */
    public int getRecipeId() {
        return _recipeId;
    }

    /**
     * @return the name of the RecipeList.
     */
    public String getRecipeName() {
        return _recipeName;
    }

    /**
     * @return the crafting success rate when using the RecipeList.
     */
    public int getSuccessRate() {
        return _successRate;
    }

    /**
     * @return the Identifier of the Item crafted with this RecipeList.
     */
    public int getItemId() {
        return _itemId;
    }

    /**
     * @return the quantity of Item crafted when using this RecipeList.
     */
    public int getCount() {
        return _count;
    }

    /**
     * @return the Identifier of the Rare Item crafted with this RecipeList.
     */
    public int getRareItemId() {
        return _rareItemId;
    }

    /**
     * @return the quantity of Rare Item crafted when using this RecipeList.
     */
    public int getRareCount() {
        return _rareCount;
    }

    /**
     * @return the chance of Rare Item crafted when using this RecipeList.
     */
    public int getRarity() {
        return _rarity;
    }

    /**
     * @return {@code true} if this a Dwarven recipe or {@code false} if its a Common recipe
     */
    public boolean isDwarvenRecipe() {
        return _isDwarvenRecipe;
    }

    /**
     * @return the table containing all Recipe (1 line of the recipe : Item-Quantity needed) of the RecipeList.
     */
    public Recipe[] getRecipes() {
        return _recipes;
    }

    /**
     * @return the table containing all RecipeStat of the statUse parameter of the RecipeList.
     */
    public RecipeStat[] getStatUse() {
        return _statUse;
    }

    /**
     * @return the table containing all RecipeStat of the AltStatChange parameter of the RecipeList.
     */
    public RecipeStat[] getAltStatChange() {
        return _altStatChange;
    }
}
