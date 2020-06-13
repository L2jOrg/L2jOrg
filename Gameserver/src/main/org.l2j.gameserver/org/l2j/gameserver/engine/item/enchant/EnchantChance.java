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

import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.EnumSet;

/**
 * @author JoeAlisson
 */
public record EnchantChance(RangedChanceGroup group, EnumSet<BodyPart>slots, Boolean magicWeapon) {

    public boolean isValid(Item item) {
        if(!slots.isEmpty() && !slots.contains(item.getBodyPart())) {
            return false;
        }
        if(magicWeapon != null && item.isMagicWeapon() != magicWeapon) {
            return false;
        }
        return group.isValid(item);
    }
}
