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

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class TradeItem {
    private final int[] attributeDefense = {0, 0, 0, 0, 0, 0};
    private final ItemTemplate template;
    private int objectId;
    private long count;
    private int enchant;
    private VariationInstance augmentation;
    private final int locationSlot;
    private final int type1;
    private final int type2;
    private Collection<EnsoulOption> soulCrystalOptions;
    private Collection<EnsoulOption> soulCrystalSpecialOptions;
    private final int[] enchantOptions;
    private byte elemAtkType;
    private int elemAtkPower;

    private long _price;

    private long _storeCount;
    private int _augmentationOption1 = -1;
    private int _augmentationOption2 = -1;

    public TradeItem(Item item, long count, long price) {
        Objects.requireNonNull(item);
        objectId = item.getObjectId();
        template = item.getTemplate();
        locationSlot = item.getLocationSlot();
        enchant = item.getEnchantLevel();
        type1 = item.getCustomType1();
        type2 = item.getType2();
        this.count = count;
        _price = price;
        elemAtkType = item.getAttackAttributeType().getClientId();
        elemAtkPower = item.getAttackAttributePower();
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            attributeDefense[type.getClientId()] = item.getDefenceAttribute(type);
        }
        enchantOptions = item.getEnchantOptions();
        soulCrystalOptions = item.getSpecialAbilities();
        soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();

        augmentation = item.getAugmentation();
        if (item.getAugmentation() != null) {
            _augmentationOption1 = item.getAugmentation().getOption1Id();
            _augmentationOption1 = item.getAugmentation().getOption2Id();
        }
    }

    public TradeItem(ItemTemplate item, long count, long price) {
        Objects.requireNonNull(item);
        objectId = 0;
        template = item;
        locationSlot = 0;
        enchant = 0;
        type1 = 0;
        type2 = 0;
        this.count = count;
        _storeCount = count;
        _price = price;
        elemAtkType = AttributeType.NONE.getClientId();
        elemAtkPower = 0;
        enchantOptions = Item.DEFAULT_ENCHANT_OPTIONS;
        soulCrystalOptions = Collections.emptyList();
        soulCrystalSpecialOptions = Collections.emptyList();
    }

    public TradeItem(TradeItem item, long count, long price) {
        Objects.requireNonNull(item);
        objectId = item.getObjectId();
        template = item.getItem();
        locationSlot = item.getLocationSlot();
        enchant = item.getEnchant();
        type1 = item.getCustomType1();
        type2 = item.getCustomType2();
        this.count = count;
        _storeCount = count;
        _price = price;
        elemAtkType = item.getAttackElementType();
        elemAtkPower = item.getAttackElementPower();
        for (byte i = 0; i < 6; i++) {
            attributeDefense[i] = item.getElementDefAttr(i);
        }
        enchantOptions = item.getEnchantOptions();
        soulCrystalOptions = item.getSoulCrystalOptions();
        soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public ItemTemplate getItem() {
        return template;
    }

    public int getLocationSlot() {
        return locationSlot;
    }

    public int getEnchant() {
        return enchant;
    }

    public void setEnchant(int enchant) {
        this.enchant = enchant;
    }

    public int getCustomType1() {
        return type1;
    }

    public int getCustomType2() {
        return type2;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getStoreCount() {
        return _storeCount;
    }

    public long getPrice() {
        return _price;
    }

    public void setPrice(long price) {
        _price = price;
    }

    public byte getAttackElementType() {
        return elemAtkType;
    }

    public void setAttackElementType(AttributeType attackElement) {
        elemAtkType = attackElement.getClientId();
    }

    public int getAttackElementPower() {
        return elemAtkPower;
    }

    public void setAttackElementPower(int attackElementPower) {
        elemAtkPower = attackElementPower;
    }

    public void setElementDefAttr(AttributeType element, int value) {
        attributeDefense[element.getClientId()] = value;
    }

    public int getElementDefAttr(byte i) {
        return attributeDefense[i];
    }

    public int[] getEnchantOptions() {
        return enchantOptions;
    }

    public Collection<EnsoulOption> getSoulCrystalOptions() {
        return soulCrystalOptions == null ? Collections.emptyList() : soulCrystalOptions;
    }

    public void setSoulCrystalOptions(Collection<EnsoulOption> soulCrystalOptions) {
        this.soulCrystalOptions = soulCrystalOptions;
    }

    public Collection<EnsoulOption> getSoulCrystalSpecialOptions() {
        return soulCrystalSpecialOptions == null ? Collections.emptyList() : soulCrystalSpecialOptions;
    }

    public void setSoulCrystalSpecialOptions(Collection<EnsoulOption> soulCrystalSpecialOptions) {
        this.soulCrystalSpecialOptions = soulCrystalSpecialOptions;
    }

    public void setAugmentation(int option1, int option2) {
        _augmentationOption1 = option1;
        _augmentationOption2 = option2;
    }

    public int getAugmentationOption1() {
        return _augmentationOption1;
    }

    public int getAugmentationOption2() {
        return _augmentationOption2;
    }
}
