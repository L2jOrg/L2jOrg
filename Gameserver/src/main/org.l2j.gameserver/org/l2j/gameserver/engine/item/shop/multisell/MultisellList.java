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
package org.l2j.gameserver.engine.item.shop.multisell;

import io.github.joealisson.primitive.IntSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JoeAlisson
 */
public record MultisellList(int id, List<MultisellItem> items, IntSet allowedNpcs, boolean applyTaxes, boolean chanceBased, boolean maintainEnchantment, boolean gmOnly, double ingredientMultiplier, double productMultiplier) {

    public boolean isNpcAllowed(int npcId) {
        return allowedNpcs.contains(npcId);
    }

    public List<MultisellItem> filterItemsWithIngredientId(int itemId) {
        List<MultisellItem> filteredItems = new ArrayList<>();
        for (var item : items) {
            if(item.containsIngredient(itemId)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public int size() {
        return items.size();
    }

    public MultisellItem get(int index) {
        return items.get(index);
    }
}
