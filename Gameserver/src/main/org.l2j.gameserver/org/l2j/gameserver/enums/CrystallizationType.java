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
package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.items.Armor;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.Weapon;

/**
 * @author Nik
 */
public enum CrystallizationType {
    NONE,
    WEAPON,
    ARMOR,
    ACCESORY;

    public static CrystallizationType getByItem(ItemTemplate item) {
        if (item instanceof Weapon) {
            return WEAPON;
        }
        if (item instanceof Armor) {
            return ARMOR;
        }
        if ((item.getBodyPart() == ItemTemplate.SLOT_R_EAR) //
                || (item.getBodyPart() == ItemTemplate.SLOT_L_EAR) //
                || (item.getBodyPart() == ItemTemplate.SLOT_R_FINGER) //
                || (item.getBodyPart() == ItemTemplate.SLOT_L_FINGER) //
                || (item.getBodyPart() == ItemTemplate.SLOT_NECK) //
                || (item.getBodyPart() == ItemTemplate.SLOT_HAIR) //
                || (item.getBodyPart() == ItemTemplate.SLOT_HAIR2) //
                || (item.getBodyPart() == ItemTemplate.SLOT_HAIRALL) //
                || (item.getBodyPart() == ItemTemplate.SLOT_ARTIFACT_BOOK) //
                || (item.getBodyPart() == ItemTemplate.SLOT_ARTIFACT)) {
            return ACCESORY;
        }

        return NONE;
    }
}
