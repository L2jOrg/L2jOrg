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
