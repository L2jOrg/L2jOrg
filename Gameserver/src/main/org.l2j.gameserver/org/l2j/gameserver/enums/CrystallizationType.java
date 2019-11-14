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
import org.l2j.gameserver.model.items.BodyPart;
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
        if ((item.getBodyPart() == BodyPart.RIGHT_EAR) //
                || (item.getBodyPart() == BodyPart.LEFT_EAR) //
                || (item.getBodyPart() == BodyPart.RIGHT_FINGER) //
                || (item.getBodyPart() == BodyPart.LEFT_FINGER) //
                || (item.getBodyPart() == BodyPart.NECK) //
                || (item.getBodyPart() == BodyPart.HAIR) //
                || (item.getBodyPart() == BodyPart.HAIR2) //
                || (item.getBodyPart() == BodyPart.HAIR_ALL) //
                || (item.getBodyPart() == BodyPart.ARTIFACT_BOOK) //
                || (item.getBodyPart() == BodyPart.ARTIFACT)) {
            return ACCESORY;
        }

        return NONE;
    }
}
