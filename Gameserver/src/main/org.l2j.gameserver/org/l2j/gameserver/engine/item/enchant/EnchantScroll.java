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
package org.l2j.gameserver.engine.item.enchant;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.EtcItemType;

import static org.l2j.commons.util.Util.isBetween;
import static org.l2j.gameserver.model.item.type.EtcItemType.CURSED_ENCHANT_ARMOR;
import static org.l2j.gameserver.model.item.type.EtcItemType.CURSED_ENCHANT_WEAPON;

/**
 * @author JoeAlisson
 */
public record EnchantScroll(ScrollGroup group, CrystalType grade, EtcItemType type, IntSet items, int minEnchant, int maxEnchant, float chanceBonus, int random, int maxEnchantRandom, int safeFailStep) {

    /**
     * reduce 1 from enchantment level
     */
    public boolean isCursed() {
        return type == CURSED_ENCHANT_WEAPON || type == CURSED_ENCHANT_ARMOR;
    }

    private boolean isRandom() {
        return switch (type) {
            case MULTI_ENCHANT_WEAPON,
                    MULTI_ENCHANT_ARMOR,
                    MULTI_INC_PROB_ENCHANT_WEAPON,
                    MULTI_INC_PROB_ENCHANT_ARMOR,
                    MULTI_ENCHANT_AGATHION,
                    MULTI_INC_ENCHANT_PROB_AGATHION -> true;
            default -> false;
        };
    }

    /**
     * Enchantment conserve the enchantment level on fail
     */
    public boolean isSafe() {
        return switch (type) {
            case ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_ARMOR,
                ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON,
                ANCIENT_CRYSTAL_ENCHANT_AGATHION -> true;
            default -> false;
        };
    }

    private boolean isForWeapon() {
        return switch (type) {
            case ENCHANT_WEAPON,
                    BLESSED_ENCHANT_WEAPON,
                    INC_PROP_ENCHANT_WEAPON,
                    ENCHT_ATTR_CRYSTAL_ENCHANT_WEAPON,
                    ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON,
                    BLESS_INC_PROP_ENCHANT_WEAPON,
                    MULTI_ENCHANT_WEAPON,
                    MULTI_INC_PROB_ENCHANT_WEAPON,
                    POLY_ENCHANT_WEAPON,
                    POLY_INC_ENCHANT_PROP_WEAPON,
                    CURSED_ENCHANT_WEAPON -> true;
            default -> false;
        };
    }

    /**
     * Enchantment Doesn't break the item on fail returns to +0
     */
    public boolean isBlessed() {
        return switch (type) {
            case BLESSED_ENCHANT_WEAPON,
                    BLESSED_ENCHANT_ARMOR,
                    BLESS_INC_PROP_ENCHANT_WEAPON,
                    BLESS_INC_PROP_ENCHANT_ARMOR,
                    BLESS_ENCHANT_AGATHION,
                    BLESS_INC_ENCHANT_PROP_AGATHION -> true;
            default -> false;
        };
    }

    public boolean canEnchant(Item item) {
        return isBetween(item.getEnchantLevel(), minEnchant, maxEnchant) && checkScrollRequirements(item)
                && (items.isEmpty() || items.contains(item.getId()));
    }

    private boolean checkScrollRequirements(Item item) {
        return item.isEnchantable() && grade == item.getCrystalType() && checkItemType(item);
    }

    private boolean checkItemType(Item item) {
        final var subType = item.getType2();
        if (isForWeapon()) {
            return subType == ItemTemplate.TYPE2_WEAPON;
        } else {
            return subType == ItemTemplate.TYPE2_SHIELD_ARMOR || subType == ItemTemplate.TYPE2_ACCESSORY;
        }
    }

    public boolean calcEnchantmentSuccess(Item item, double enchantRateBonus) {
        final var chance = group.enchantChanceForItem(item);
        return Rnd.chance((chance + enchantRateBonus) * chanceBonus);
    }

    public int enchantStep(Item item) {
        if (isRandom() && item.getEnchantLevel() <= maxEnchantRandom) {
            return Rnd.get(1, random);
        }
        return isCursed() ? -1 : 1;
    }
}
