/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.RecipeList;

/**
 * @author JoeAlisson
 */
@Table("character_recipeshoplist")
public class ManufactureItem {
    private int recipeId;
    private long price;
    @Column("charId")
    private int playerId;

    @NonUpdatable
    private boolean isDwarven;

    public static ManufactureItem of(int id, long price) {
        var data = new ManufactureItem();
        data.recipeId = id;
        data.price = price;
        data.isDwarven = Util.falseIfNullOrElse(RecipeData.getInstance().getRecipeList(id), RecipeList::isDwarvenRecipe);
        return data;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public long getPrice() {
        return price;
    }

    public boolean isDwarven() {
        return isDwarven;
    }

    public void setDwarven(boolean dwarven) {
        isDwarven = dwarven;
    }

    public void setOwner(int playerId) {
        this.playerId = playerId;
    }
}
