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
package org.l2j.gameserver.model.commission;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NosBit
 */
public enum CommissionTreeType {
    WEAPON(
            0,
            CommissionItemType.ONE_HAND_SWORD,
            CommissionItemType.ONE_HAND_MAGIC_SWORD,
            CommissionItemType.DAGGER,
            CommissionItemType.RAPIER,
            CommissionItemType.TWO_HAND_SWORD,
            CommissionItemType.ANCIENT_SWORD,
            CommissionItemType.DUALSWORD,
            CommissionItemType.DUAL_DAGGER,
            CommissionItemType.BLUNT_WEAPON,
            CommissionItemType.ONE_HAND_MAGIC_BLUNT_WEAPON,
            CommissionItemType.TWO_HAND_BLUNT_WEAPON,
            CommissionItemType.TWO_HAND_MAGIC_BLUNT_WEAPON,
            CommissionItemType.DUAL_BLUNT_WEAPON,
            CommissionItemType.BOW,
            CommissionItemType.CROSSBOW,
            CommissionItemType.FIST_WEAPON,
            CommissionItemType.SPEAR,
            CommissionItemType.OTHER_WEAPON),
    ARMOR(1, CommissionItemType.HELMET, CommissionItemType.ARMOR_TOP, CommissionItemType.ARMOR_PANTS, CommissionItemType.FULL_BODY, CommissionItemType.GLOVES, CommissionItemType.FEET, CommissionItemType.SHIELD, CommissionItemType.SIGIL, CommissionItemType.UNDERWEAR, CommissionItemType.CLOAK),
    ACCESSORY(2, CommissionItemType.RING, CommissionItemType.EARRING, CommissionItemType.NECKLACE, CommissionItemType.BELT, CommissionItemType.BRACELET, CommissionItemType.HAIR_ACCESSORY),
    SUPPLIES(3, CommissionItemType.POTION, CommissionItemType.SCROLL_ENCHANT_WEAPON, CommissionItemType.SCROLL_ENCHANT_ARMOR, CommissionItemType.SCROLL_OTHER, CommissionItemType.SOULSHOT, CommissionItemType.SPIRITSHOT),
    PET_GOODS(4, CommissionItemType.PET_EQUIPMENT, CommissionItemType.PET_SUPPLIES),
    MISC(
            5,
            CommissionItemType.CRYSTAL,
            CommissionItemType.RECIPE,
            CommissionItemType.MAJOR_CRAFTING_INGREDIENTS,
            CommissionItemType.LIFE_STONE,
            CommissionItemType.SOUL_CRYSTAL,
            CommissionItemType.ATTRIBUTE_STONE,
            CommissionItemType.WEAPON_ENCHANT_STONE,
            CommissionItemType.ARMOR_ENCHANT_STONE,
            CommissionItemType.SPELLBOOK,
            CommissionItemType.GEMSTONE,
            CommissionItemType.POUCH,
            CommissionItemType.PIN,
            CommissionItemType.MAGIC_RUNE_CLIP,
            CommissionItemType.MAGIC_ORNAMENT,
            CommissionItemType.DYES,
            CommissionItemType.OTHER_ITEM);

    private final int _clientId;
    private final Set<CommissionItemType> _commissionItemTypes;

    CommissionTreeType(int clientId, CommissionItemType... commissionItemTypes) {
        _clientId = clientId;
        _commissionItemTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(commissionItemTypes)));
    }

    /**
     * Finds the commission tree type by the client id
     *
     * @param clientId the client id
     * @return the commission tree type if its found, {@code null} otherwise
     */
    public static CommissionTreeType findByClientId(int clientId) {
        for (CommissionTreeType value : values()) {
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

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public Set<CommissionItemType> getCommissionItemTypes() {
        return _commissionItemTypes;
    }
}
