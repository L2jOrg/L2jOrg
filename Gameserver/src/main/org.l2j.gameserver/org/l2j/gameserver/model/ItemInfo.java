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
package org.l2j.gameserver.model;

import org.l2j.gameserver.engine.item.EnsoulOption;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemChangeType;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.engine.item.ItemTemplate;

import java.util.Objects;

/**
 * Get all information from Item to generate ItemInfo.
 */
public class ItemInfo {

    private ItemTemplate template;
    private int objectId;
    private long count;
    private int enchant;
    private VariationInstance augmentation;
    private int locationSlot;
    private int type2;
    private EnsoulOption soulCrystalOption;
    private EnsoulOption soulCrystalSpecialOption;
    private int[] enchantOption;
    private int time;

    private ItemChangeType change;
    private boolean _available = true;
    private int _equipped;
    private int reuse;

    /**
     * Get all information from Item to generate ItemInfo.
     *
     * @param item
     */
    public ItemInfo(Item item) {
        Objects.requireNonNull(item);

        // Get the Identifier of the Item
        objectId = item.getObjectId();

        // Get the ItemTemplate of the Item
        template = item.getTemplate();

        // Get the enchant level of the Item
        enchant = item.getEnchantLevel();

        // Get the augmentation bonus
        augmentation = item.getAugmentation();

        // Get the quantity of the Item
        count = item.getCount();

        // Get custom item types (used loto, race tickets)
        type2 = item.getType2();

        // Verify if the Item is equipped
        _equipped = item.isEquipped() ? 1 : 0;

        change = item.getLastChange();

        time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -9999;
        _available = item.isAvailable();
        locationSlot = item.getLocationSlot();

        enchantOption = item.getEnchantOptions();
        soulCrystalOption = item.getSpecialAbility();
        soulCrystalSpecialOption = item.getAdditionalSpecialAbility();
    }

    public ItemInfo(Item item, ItemChangeType change) {
        this(item);
        this.change = change;
    }

    public ItemInfo(TradeItem item) {
        if (item == null) {
            return;
        }

        // Get the Identifier of the Item
        objectId = item.getObjectId();

        // Get the ItemTemplate of the Item
        template = item.getItem();

        // Get the enchant level of the Item
        enchant = item.getEnchant();

        // Get the augmentation bonus
        if ((item.getAugmentationOption1() >= 0) && (item.getAugmentationOption2() >= 0)) {
            augmentation = new VariationInstance(item.getObjectId(), 0, item.getAugmentationOption1(), item.getAugmentationOption2());
        }

        // Get the quantity of the Item
        count = item.getCount();

        // Get custom item types (used loto, race tickets)
        type2 = item.getCustomType2();

        // Verify if the Item is equipped
        _equipped = 0;

        // Get the action to do clientside
        change = ItemChangeType.MODIFIED;

        time = -9999;

        locationSlot = item.getLocationSlot();

        enchantOption = item.getEnchantOptions();
        soulCrystalOption = item.getSoulCrystalOption();
        soulCrystalSpecialOption = item.getSoulCrystalSpecialOption();
    }

    public ItemInfo(Product item) {
        if (item == null) {
            return;
        }

        template = item.getTemplate();
        count = item.getCount();
        type2 = template.getType2();
        change = ItemChangeType.MODIFIED;
        time = -9999;
    }

    public int getObjectId() {
        return objectId;
    }

    public ItemTemplate getTemplate() {
        return template;
    }

    public int getEnchantLevel() {
        return enchant;
    }

    public VariationInstance getAugmentation() {
        return augmentation;
    }

    public long getCount() {
        return count;
    }

    public int getEquipped() {
        return _equipped;
    }

    public ItemChangeType getChange() {
        return change;
    }

    public int getTime() {
        return time > 0 ? time : -9999;
    }

    public boolean isAvailable() {
        return _available;
    }

    public int getLocationSlot() {
        return locationSlot;
    }

    public int[] getEnchantOptions() {
        return enchantOption;
    }

    public EnsoulOption getSoulCrystalOption() {
        return soulCrystalOption;
    }

    public EnsoulOption getSoulCrystalSpecialOption() {
        return soulCrystalSpecialOption;
    }

    public int getId() {
        return template.getId();
    }

    @Override
    public String toString() {
        return template + "[objId: " + objectId + ", count: " + count + "]";
    }

    public BodyPart getBodyPart() {
        return template.getBodyPart();
    }

    public int getType2() {
        return template.getType2();
    }

    public int getDisplayId() {
        return template.getDisplayId();
    }

    public boolean isQuestItem() {
        return template.isQuestItem();
    }

    public int getReuse() {
        return reuse;
    }
}
