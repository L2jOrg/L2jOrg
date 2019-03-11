/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model;

/**
 * This class describes a Recipe used by Dwarf to craft Item. All L2RecipeList are made of L2RecipeInstance (1 line of the recipe : Item-Quantity needed).
 */
public class L2RecipeList {
    /**
     * The Identifier of the Instance
     */
    private final int _id;
    /**
     * The crafting level needed to use this L2RecipeList
     */
    private final int _level;
    /**
     * The Identifier of the L2RecipeList
     */
    private final int _recipeId;
    /**
     * The name of the L2RecipeList
     */
    private final String _recipeName;
    /**
     * The crafting success rate when using the L2RecipeList
     */
    private final int _successRate;
    /**
     * The Identifier of the Item crafted with this L2RecipeList
     */
    private final int _itemId;
    /**
     * The quantity of Item crafted when using this L2RecipeList
     */
    private final int _count;
    /**
     * If this a common or a dwarven recipe
     */
    private final boolean _isDwarvenRecipe;
    /**
     * The table containing all L2RecipeInstance (1 line of the recipe : Item-Quantity needed) of the L2RecipeList
     */
    private L2RecipeInstance[] _recipes;
    /**
     * The table containing all L2RecipeStatInstance for the statUse parameter of the L2RecipeList
     */
    private L2RecipeStatInstance[] _statUse;
    /**
     * The table containing all L2RecipeStatInstance for the altStatChange parameter of the L2RecipeList
     */
    private L2RecipeStatInstance[] _altStatChange;
    /**
     * The Identifier of the Rare Item crafted with this L2RecipeList
     */
    private int _rareItemId;
    /**
     * The quantity of Rare Item crafted when using this L2RecipeList
     */
    private int _rareCount;
    /**
     * The chance of Rare Item crafted when using this L2RecipeList
     */
    private int _rarity;

    /**
     * Constructor of L2RecipeList (create a new Recipe).
     *
     * @param set
     * @param haveRare
     */
    public L2RecipeList(StatsSet set, boolean haveRare) {
        _recipes = new L2RecipeInstance[0];
        _statUse = new L2RecipeStatInstance[0];
        _altStatChange = new L2RecipeStatInstance[0];
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
     * Add a L2RecipeInstance to the L2RecipeList (add a line Item-Quantity needed to the Recipe).
     *
     * @param recipe
     */
    public void addRecipe(L2RecipeInstance recipe) {
        final int len = _recipes.length;
        final L2RecipeInstance[] tmp = new L2RecipeInstance[len + 1];
        System.arraycopy(_recipes, 0, tmp, 0, len);
        tmp[len] = recipe;
        _recipes = tmp;
    }

    /**
     * Add a L2RecipeStatInstance of the statUse parameter to the L2RecipeList.
     *
     * @param statUse
     */
    public void addStatUse(L2RecipeStatInstance statUse) {
        final int len = _statUse.length;
        final L2RecipeStatInstance[] tmp = new L2RecipeStatInstance[len + 1];
        System.arraycopy(_statUse, 0, tmp, 0, len);
        tmp[len] = statUse;
        _statUse = tmp;
    }

    /**
     * Add a L2RecipeStatInstance of the altStatChange parameter to the L2RecipeList.
     *
     * @param statChange
     */
    public void addAltStatChange(L2RecipeStatInstance statChange) {
        final int len = _altStatChange.length;
        final L2RecipeStatInstance[] tmp = new L2RecipeStatInstance[len + 1];
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
     * @return the crafting level needed to use this L2RecipeList.
     */
    public int getLevel() {
        return _level;
    }

    /**
     * @return the Identifier of the L2RecipeList.
     */
    public int getRecipeId() {
        return _recipeId;
    }

    /**
     * @return the name of the L2RecipeList.
     */
    public String getRecipeName() {
        return _recipeName;
    }

    /**
     * @return the crafting success rate when using the L2RecipeList.
     */
    public int getSuccessRate() {
        return _successRate;
    }

    /**
     * @return the Identifier of the Item crafted with this L2RecipeList.
     */
    public int getItemId() {
        return _itemId;
    }

    /**
     * @return the quantity of Item crafted when using this L2RecipeList.
     */
    public int getCount() {
        return _count;
    }

    /**
     * @return the Identifier of the Rare Item crafted with this L2RecipeList.
     */
    public int getRareItemId() {
        return _rareItemId;
    }

    /**
     * @return the quantity of Rare Item crafted when using this L2RecipeList.
     */
    public int getRareCount() {
        return _rareCount;
    }

    /**
     * @return the chance of Rare Item crafted when using this L2RecipeList.
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
     * @return the table containing all L2RecipeInstance (1 line of the recipe : Item-Quantity needed) of the L2RecipeList.
     */
    public L2RecipeInstance[] getRecipes() {
        return _recipes;
    }

    /**
     * @return the table containing all L2RecipeStatInstance of the statUse parameter of the L2RecipeList.
     */
    public L2RecipeStatInstance[] getStatUse() {
        return _statUse;
    }

    /**
     * @return the table containing all L2RecipeStatInstance of the AltStatChange parameter of the L2RecipeList.
     */
    public L2RecipeStatInstance[] getAltStatChange() {
        return _altStatChange;
    }
}
