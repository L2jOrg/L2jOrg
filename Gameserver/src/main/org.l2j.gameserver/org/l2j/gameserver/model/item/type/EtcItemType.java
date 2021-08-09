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
package org.l2j.gameserver.model.item.type;

/**
 * EtcItem Type enumerated.
 *
 * @author JoeAlisson
 */
public enum EtcItemType implements ItemType {
    NONE,
    SCROLL,
    ARROW,
    POTION,
    SPELLBOOK,
    RECIPE,
    MATERIAL,
    PET_COLLAR,
    CASTLE_GUARD,
    DYE,
    SEED,
    SEED2,
    HARVEST,
    LOTTO,
    RACE_TICKET,
    TICKET_OF_LORD,
    LURE,
    CROP,
    MATURECROP,
    ENCHANT_WEAPON,
    ENCHANT_ARMOR,
    BLESSED_ENCHANT_WEAPON,
    BLESSED_ENCHANT_ARMOR,
    COUPON,
    ELIXIR,
    ENCHT_ATTR,
    ENCHT_ATTR_CURSED,
    BOLT,
    INC_PROP_ENCHANT_WEAPON,
    INC_PROP_ENCHANT_ARMOR,
    ENCHT_ATTR_CRYSTAL_ENCHANT_ARMOR,
    ENCHT_ATTR_CRYSTAL_ENCHANT_WEAPON,
    ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_ARMOR,
    ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON,
    RUNE,
    RUNE_SELECT,
    TELEPORT_BOOKMARK,
    CHANGE_ATTR,
    SOULSHOT,
    SHAPE_SHIFTING_WEAPON,
    BLESS_SHAPE_SHIFTING_WEAPON,
    SHAPE_SHIFTING_WEAPON_FIXED,
    SHAPE_SHIFTING_ARMOR,
    BLESS_SHAPE_SHIFTING_ARMOR,
    SHAPE_SHIFTING_ARMOR_FIXED,
    SHAPE_SHIFTING_HAIR_ACC,
    BLESS_SHAPE_SHIFTING_HAIR_ACC,
    SHAPE_SHIFTING_HAIR_ACC_FIXED,
    RESTORE_SHAPE_SHIFTING_WEAPON,
    RESTORE_SHAPE_SHIFTING_ARMOR,
    RESTORE_SHAPE_SHIFTING_HAIR_ACC,
    RESTORE_SHAPE_SHIFTING_ALL_ITEM,
    BLESS_INC_PROP_ENCHANT_WEAPON,
    BLESS_INC_PROP_ENCHANT_ARMOR,
    CARD_EVENT,
    SHAPE_SHIFTING_ALL_ITEM_FIXED,
    MULTI_ENCHANT_WEAPON,
    MULTI_ENCHANT_ARMOR,
    MULTI_INC_PROB_ENCHANT_WEAPON,
    MULTI_INC_PROB_ENCHANT_ARMOR,
    ENSOUL_STONE,
    NICK_COLOR_OLD,
    NICK_COLOR_NEW,
    ENCHANT_AGATHION,
    BLESS_ENCHANT_AGATHION,
    MULTI_ENCHANT_AGATHION,
    ANCIENT_CRYSTAL_ENCHANT_AGATHION,
    INC_ENCHANT_PROP_AGATHION,
    BLESS_INC_ENCHANT_PROP_AGATHION,
    MULTI_INC_ENCHANT_PROB_AGATHION,
    SEAL_SCROLL,
    UNSEAL_SCROLL,
    BULLET,
    MAGICLAMP,
    TRANSFORMATION_BOOK,
    TRANSFORMATION_BOOK_BOX_RANDOM,
    TRANSFORMATION_BOOK_BOX_RANDOM_RARE,
    TRANSFORMATION_BOOK_BOX_STANDARD,
    TRANSFORMATION_BOOK_BOX_HIGH_GRADE,
    TRANSFORMATION_BOOK_BOX_RARE,
    TRANSFORMATION_BOOK_BOX_LEGENDARY,
    TRANSFORMATION_BOOK_BOX_MYTHIC,
    POLY_ENCHANT_WEAPON,
    POLY_ENCHANT_ARMOR,
    POLY_INC_ENCHANT_PROP_WEAPON,
    POLY_INC_ENCHANT_ARMOR,
    CURSED_ENCHANT_WEAPON,
    CURSED_ENCHANT_ARMOR,
    VITAL_LEGACY_ITEM_1D,
    VITAL_LEGACY_ITEM_7D,
    VITAL_LEGACY_ITEM_30D,
    BLESSED_SCROLL;


    @Override
    public int mask() {
        return 0;
    }

    public boolean isEnchantment() {
        return switch (this) {
            case ENCHANT_WEAPON,
                ENCHANT_ARMOR,
                BLESSED_ENCHANT_WEAPON,
                BLESSED_ENCHANT_ARMOR,
                INC_PROP_ENCHANT_WEAPON,
                INC_PROP_ENCHANT_ARMOR,
                ENCHT_ATTR_CRYSTAL_ENCHANT_ARMOR,
                ENCHT_ATTR_CRYSTAL_ENCHANT_WEAPON,
                ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_ARMOR,
                ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WEAPON,
                BLESS_INC_PROP_ENCHANT_WEAPON,
                BLESS_INC_PROP_ENCHANT_ARMOR,
                MULTI_ENCHANT_WEAPON,
                MULTI_ENCHANT_ARMOR,
                MULTI_INC_PROB_ENCHANT_WEAPON,
                MULTI_INC_PROB_ENCHANT_ARMOR,
                ENCHANT_AGATHION,
                BLESS_ENCHANT_AGATHION,
                MULTI_ENCHANT_AGATHION,
                ANCIENT_CRYSTAL_ENCHANT_AGATHION,
                INC_ENCHANT_PROP_AGATHION,
                BLESS_INC_ENCHANT_PROP_AGATHION,
                MULTI_INC_ENCHANT_PROB_AGATHION,
                POLY_ENCHANT_WEAPON,
                POLY_ENCHANT_ARMOR,
                POLY_INC_ENCHANT_PROP_WEAPON,
                POLY_INC_ENCHANT_ARMOR,
                CURSED_ENCHANT_WEAPON,
                CURSED_ENCHANT_ARMOR -> true;
            default -> false;
        };
    }
}
