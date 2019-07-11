package org.l2j.gameserver.model;

/**
 * This class describes a RecipeList component (1 line of the recipe : Item-Quantity needed).
 */
public class Recipe {
    /**
     * The Identifier of the item needed in the Recipe
     */
    private final int _itemId;

    /**
     * The item quantity needed in the Recipe
     */
    private final int _quantity;

    /**
     * Constructor of Recipe (create a new line in a RecipeList).
     *
     * @param itemId
     * @param quantity
     */
    public Recipe(int itemId, int quantity) {
        _itemId = itemId;
        _quantity = quantity;
    }

    /**
     * @return the Identifier of the Recipe Item needed.
     */
    public int getItemId() {
        return _itemId;
    }

    /**
     * @return the Item quantity needed of the Recipe.
     */
    public int getQuantity() {
        return _quantity;
    }
}
