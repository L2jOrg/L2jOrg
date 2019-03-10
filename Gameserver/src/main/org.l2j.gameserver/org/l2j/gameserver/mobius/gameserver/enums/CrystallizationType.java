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
package org.l2j.gameserver.mobius.gameserver.enums;

import org.l2j.gameserver.mobius.gameserver.model.items.L2Armor;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Weapon;

/**
 * @author Nik
 */
public enum CrystallizationType {
    NONE,
    WEAPON,
    ARMOR,
    ACCESORY;

    public static CrystallizationType getByItem(L2Item item) {
        if (item instanceof L2Weapon) {
            return WEAPON;
        }
        if (item instanceof L2Armor) {
            return ARMOR;
        }
        if ((item.getBodyPart() == L2Item.SLOT_R_EAR) //
                || (item.getBodyPart() == L2Item.SLOT_L_EAR) //
                || (item.getBodyPart() == L2Item.SLOT_R_FINGER) //
                || (item.getBodyPart() == L2Item.SLOT_L_FINGER) //
                || (item.getBodyPart() == L2Item.SLOT_NECK) //
                || (item.getBodyPart() == L2Item.SLOT_HAIR) //
                || (item.getBodyPart() == L2Item.SLOT_HAIR2) //
                || (item.getBodyPart() == L2Item.SLOT_HAIRALL) //
                || (item.getBodyPart() == L2Item.SLOT_ARTIFACT_BOOK) //
                || (item.getBodyPart() == L2Item.SLOT_ARTIFACT)) {
            return ACCESORY;
        }

        return NONE;
    }
}
