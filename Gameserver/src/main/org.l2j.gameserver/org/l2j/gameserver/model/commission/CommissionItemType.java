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
package org.l2j.gameserver.model.commission;

/**
 * @author NosBit
 */
public enum CommissionItemType {
    // Weapon
    ONE_HAND_SWORD(1),
    ONE_HAND_MAGIC_SWORD(2),
    DAGGER(3),
    RAPIER(4),
    TWO_HAND_SWORD(5),
    ANCIENT_SWORD(6),
    DUALSWORD(7),
    DUAL_DAGGER(8),
    BLUNT_WEAPON(9),
    ONE_HAND_MAGIC_BLUNT_WEAPON(10),
    TWO_HAND_BLUNT_WEAPON(11),
    TWO_HAND_MAGIC_BLUNT_WEAPON(12),
    DUAL_BLUNT_WEAPON(13),
    BOW(14),
    CROSSBOW(15),
    FIST_WEAPON(16),
    SPEAR(17),
    OTHER_WEAPON(18),
    // Armor
    HELMET(19),
    ARMOR_TOP(20),
    ARMOR_PANTS(21),
    FULL_BODY(22),
    GLOVES(23),
    FEET(24),
    SHIELD(25),
    SIGIL(26),
    UNDERWEAR(27),
    CLOAK(28),
    // Accessory
    RING(29),
    EARRING(30),
    NECKLACE(31),
    BELT(32),
    BRACELET(33),
    HAIR_ACCESSORY(34),
    // Supplies
    POTION(35),
    SCROLL_ENCHANT_WEAPON(36),
    SCROLL_ENCHANT_ARMOR(37),
    SCROLL_OTHER(38),
    SOULSHOT(39),
    SPIRITSHOT(40),
    // Pet Goods
    PET_EQUIPMENT(42),
    PET_SUPPLIES(43),
    // Misc.
    CRYSTAL(44),
    RECIPE(45),
    MAJOR_CRAFTING_INGREDIENTS(46),
    LIFE_STONE(47),
    SOUL_CRYSTAL(48),
    ATTRIBUTE_STONE(49),
    WEAPON_ENCHANT_STONE(50),
    ARMOR_ENCHANT_STONE(51),
    SPELLBOOK(52),
    GEMSTONE(53),
    POUCH(54),
    PIN(55),
    MAGIC_RUNE_CLIP(56),
    MAGIC_ORNAMENT(57),
    DYES(58),
    OTHER_ITEM(59);

    private final int _clientId;

    CommissionItemType(int clientId) {
        _clientId = clientId;
    }

    /**
     * Finds the commission item type by the client id
     *
     * @param clientId the client id
     * @return the commission item type if its found, {@code null} otherwise
     */
    public static CommissionItemType findByClientId(int clientId) {
        for (CommissionItemType value : values()) {
            if (value.getClientId() == clientId) {
                return value;
            }
        }
        return null;
    }

    /**
     * Gets the client id.
     *
     * @return the client id
     */
    public int getClientId() {
        return _clientId;
    }
}
