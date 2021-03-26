/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.ItemContainer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author Nik
 * @author JoeAlisson
 */
public class PreparedMultisellList {
    private final MultisellList template;
    private final boolean inventoryOnly;
    private final double ingredientMultiplier;
    private final double productMultiplier;

    private List<ItemInfo> itemInfos;
    private List<MultisellItem> filteredItems;
    private double taxRate;

    public PreparedMultisellList(MultisellList template, boolean inventoryOnly, ItemContainer inventory, Npc npc, double ingredientMultiplier, double productMultiplier) {
        this.template = template;
        this.inventoryOnly = inventoryOnly;
        this.ingredientMultiplier = ingredientMultiplier;
        this.productMultiplier = productMultiplier;

        if (nonNull(npc)) {
            taxRate = npc.getCastleTaxRate(TaxType.BUY);
        }
        
        if (inventoryOnly) {
            filterItemsBasedOnInventory(template, inventory);
        }
    }

    private void filterItemsBasedOnInventory(MultisellList template, ItemContainer inventory) {
        filteredItems = new ArrayList<>();
        itemInfos = new ArrayList<>();

        for (Item item : inventory.getItems(this::filterNotUsingEquip)) {
            ItemInfo info = new ItemInfo(item);
            for (MultisellItem multisellItem : template.filterItemsWithIngredientId(item.getId())) {
                filteredItems.add(multisellItem);
                itemInfos.add(info);
            }
        }
    }

    private boolean filterNotUsingEquip(Item item) {
        return item.isEquipable() && !item.isEquipped();
    }

    public ItemInfo getItemEnchantment(int index) {
        return nonNull(itemInfos) ? itemInfos.get(index) : null;
    }

    public double getTaxRate() {
        return template.applyTaxes() ? taxRate : 0;
    }

    public boolean isInventoryOnly() {
        return inventoryOnly;
    }

    public long getIngredientCount(MultisellIngredient ingredient) {
        return (ingredient.id() == CommonItem.ADENA) ? Math.round(ingredient.count() * getIngredientMultiplier() * (1 + getTaxRate())) : Math.round(ingredient.count() * getIngredientMultiplier());
    }

    public double getIngredientMultiplier() {
        return Double.isNaN(ingredientMultiplier) ? template.ingredientMultiplier() : ingredientMultiplier;
    }
    
    public long getProductCount(MultisellProduct product) {
        return Math.round(product.count() * getProductMultiplier());
    }

    public double getProductMultiplier() {
        return Double.isNaN(productMultiplier) ? template.productMultiplier() : productMultiplier; 
    }

    public int size() {
        return inventoryOnly ? filteredItems.size() : template.size();
    }

    public int id() {
        return template.id();
    }

    public boolean isChanceBased() {
        return template.chanceBased();
    }

    public boolean maintainEnchantment() {
        return template.maintainEnchantment();
    }

    public MultisellItem get(int index) {
        return inventoryOnly ? filteredItems.get(index) : template.get(index);
    }

    public boolean isNpcAllowed(int npcId) {
        return template.isNpcAllowed(npcId);
    }

    public boolean applyTaxes() {
        return template.applyTaxes();
    }
}