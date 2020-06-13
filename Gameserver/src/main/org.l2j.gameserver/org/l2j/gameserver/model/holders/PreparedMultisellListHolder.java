/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.ItemContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * A modified version of {@link MultisellListHolder} that may include altered data of the original and other dynamic data resulted from players' interraction.
 *
 * @author Nik
 */
public class PreparedMultisellListHolder extends MultisellListHolder {
    private final boolean _inventoryOnly;
    private int _npcObjectId;
    private double _taxRate;
    private List<ItemInfo> _itemInfos;

    public PreparedMultisellListHolder(MultisellListHolder list, boolean inventoryOnly, ItemContainer inventory, Npc npc, double ingredientMultiplier, double productMultiplier) {
        super(list.getId(), list.isChanceMultisell(), list.isApplyTaxes(), list.isMaintainEnchantment(), list.getIngredientMultiplier(), list.getProductMultiplier(), list._entries, list._npcsAllowed);

        _inventoryOnly = inventoryOnly;

        if (npc != null) {
            _npcObjectId = npc.getObjectId();
            _taxRate = npc.getCastleTaxRate(TaxType.BUY);
        }

        // Display items from inventory that are available for exchange.
        if (inventoryOnly) {
            _entries = new ArrayList<>();
            _itemInfos = new ArrayList<>();

            // Only do the match up on equippable items that are not currently equipped. For each appropriate item, produce a set of entries for the multisell list.
            inventory.getItems(item -> !item.isEquipped() && (item.isArmor() || item.isWeapon())).forEach(item ->
            {
                // Check ingredients of each entry to see if it's an entry we'd like to include.
                list.getEntries().stream().filter(e -> e.getIngredients().stream().anyMatch(i -> i.getId() == item.getId())).forEach(e ->
                {
                    _entries.add(e);
                    _itemInfos.add(new ItemInfo(item));
                });
            });
        }
    }

    public ItemInfo getItemEnchantment(int index) {
        return _itemInfos != null ? _itemInfos.get(index) : null;
    }

    public double getTaxRate() {
        return isApplyTaxes() ? _taxRate : 0;
    }

    public boolean isInventoryOnly() {
        return _inventoryOnly;
    }

    public final boolean checkNpcObjectId(int npcObjectId) {
        return (_npcObjectId == 0) || (_npcObjectId == npcObjectId);
    }

    /**
     * @param ingredient
     * @return the new count of the given ingredient after applying ingredient multiplier and adena tax rate.
     */
    public long getIngredientCount(ItemHolder ingredient) {
        return (ingredient.getId() == CommonItem.ADENA) ? Math.round(ingredient.getCount() * getIngredientMultiplier() * (1 + getTaxRate())) : Math.round(ingredient.getCount() * getIngredientMultiplier());
    }

    /**
     * @param product
     * @return the new count of the given product after applying product multiplier.
     */
    public long getProductCount(ItemChanceHolder product) {
        return Math.round(product.getCount() * getProductMultiplier());
    }
}