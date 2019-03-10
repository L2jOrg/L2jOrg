/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.items.type;

/**
 * EtcItem Type enumerated.
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
    ENCHT_WP,
    ENCHT_AM,
    GIANT_ENCHT_WP,
    GIANT_ENCHT_AM,
    BLESS_ENCHT_WP,
    BLESS_ENCHT_AM,
    COUPON,
    ELIXIR,
    ENCHT_ATTR,
    ENCHT_ATTR_CURSED,
    BOLT,
    ENCHT_ATTR_INC_PROP_ENCHT_WP,
    ENCHT_ATTR_INC_PROP_ENCHT_AM,
    BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_WP,
    BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_AM,
    BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP,
    BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM,
    GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP,
    GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM,
    ENCHT_ATTR_CRYSTAL_ENCHANT_AM,
    ENCHT_ATTR_CRYSTAL_ENCHANT_WP,
    ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_AM,
    ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WP,
    ENCHT_ATTR_RUNE,
    ENCHT_ATTRT_RUNE_SELECT,
    TELEPORTBOOKMARK,
    CHANGE_ATTR,
    SOULSHOT,
    SHAPE_SHIFTING_WP,
    BLESS_SHAPE_SHIFTING_WP,
    // EIT_RESTORE_SHAPE_SHIFTING_WP,
    SHAPE_SHIFTING_WP_FIXED,
    SHAPE_SHIFTING_AM,
    BLESS_SHAPE_SHIFTING_AM,
    SHAPE_SHIFTING_AM_FIXED,
    SHAPE_SHIFTING_HAIRACC,
    BLESS_SHAPE_SHIFTING_HAIRACC,
    SHAPE_SHIFTING_HAIRACC_FIXED,
    RESTORE_SHAPE_SHIFTING_WP,
    RESTORE_SHAPE_SHIFTING_AM,
    RESTORE_SHAPE_SHIFTING_HAIRACC,
    RESTORE_SHAPE_SHIFTING_ALLITEM,
    BLESS_INC_PROP_ENCHT_WP,
    BLESS_INC_PROP_ENCHT_AM,
    CARD_EVENT,
    SHAPE_SHIFTING_ALLITEM_FIXED,
    MULTI_ENCHT_WP,
    MULTI_ENCHT_AM,
    MULTI_INC_PROB_ENCHT_WP,
    MULTI_INC_PROB_ENCHT_AM,
    SOUL_CRYSTAL,
    ENSOUL_STONE;

    /**
     * @return the ID of the item after applying the mask.
     */
    @Override
    public int mask() {
        return 0;
    }
}
