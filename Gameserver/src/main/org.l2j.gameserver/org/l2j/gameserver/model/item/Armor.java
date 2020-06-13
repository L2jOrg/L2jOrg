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
package org.l2j.gameserver.model.item;

import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.item.type.CrystalType;

import static org.l2j.gameserver.model.item.BodyPart.*;

/**
 * This class is dedicated to the management of armors.
 *
 * @author JoeAlisson
 */
public final class Armor extends ItemTemplate implements EquipableItem {
    private ArmorType type;

    public Armor(int id, String name, ArmorType type, BodyPart bodyPart) {
        super(id, name);
        this.type = type;
        this.bodyPart = bodyPart;

        if(bodyPart.isAnyOf(NECK, EAR, FINGER, RIGHT_BRACELET, LEFT_BRACELET, ARTIFACT_BOOK)) {
            type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
            type2 = TYPE2_ACCESSORY;
        } else {
            type1 = TYPE1_SHIELD_ARMOR;
            type2 = TYPE2_SHIELD_ARMOR;
        }
    }

    /**
     * @return the type of the armor.
     */
    @Override
    public ArmorType getItemType() {
        return type;
    }

    /**
     * @return the ID of the item after applying the mask.
     */
    @Override
    public final int getItemMask() {
        return type.mask();
    }

    @Override
    public void setCrystalType(CrystalType type) {
        this.crystalType = type;
    }

    @Override
    public void setCrystalCount(int count) {
        this.crystalCount = count;
    }

    public void setEnchantable(Boolean enchantable) {
        this.enchantable = enchantable;
    }

    public void setEquipReuseDelay(int equipReuseDelay) {
        this.equipReuseDelay = equipReuseDelay;
    }
}
