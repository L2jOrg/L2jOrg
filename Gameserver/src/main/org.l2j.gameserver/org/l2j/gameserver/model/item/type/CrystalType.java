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
package org.l2j.gameserver.model.item.type;

import java.util.function.Consumer;

/**
 * Crystal Type enumerated.
 *
 * @author Adry_85
 */
public enum CrystalType {
    NONE(0, 0, 0, 0),
    D(1, 1458, 11, 90),
    C(2, 1459, 6, 45),
    B(3, 1460, 11, 67),
    A(4, 1461, 20, 145),
    S(5, 1462, 25, 250),
    EVENT(11, 0, 0, 0);

    private final int id;
    private final int crystalId;
    private final int crystalEnchantBonusArmor;
    private final int crystalEnchantBonusWeapon;

    private static final CrystalType[] CACHED = values();

    CrystalType(int id, int crystalId, int crystalEnchantBonusArmor, int crystalEnchantBonusWeapon) {
        this.id = id;
        this.crystalId = crystalId;
        this.crystalEnchantBonusArmor = crystalEnchantBonusArmor;
        this.crystalEnchantBonusWeapon = crystalEnchantBonusWeapon;
    }

    /**
     * Gets the crystal type ID.
     *
     * @return the crystal type ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the item ID of the crystal.
     *
     * @return the item ID of the crystal
     */
    public int getCrystalId() {
        return crystalId;
    }

    public int getCrystalEnchantBonusArmor() {
        return crystalEnchantBonusArmor;
    }

    public int getCrystalEnchantBonusWeapon() {
        return crystalEnchantBonusWeapon;
    }

    public static void forEach(Consumer<CrystalType> action) {
        for (CrystalType crystalType : CACHED) {
            action.accept(crystalType);
        }
    }

}
