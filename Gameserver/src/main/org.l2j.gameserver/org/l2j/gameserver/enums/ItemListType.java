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
package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

/**
 * @author UnAfraid
 */
public enum ItemListType implements IUpdateTypeComponent {
    AUGMENT_BONUS(0x01),
    ELEMENTAL_ATTRIBUTE(0x02),
    ENCHANT_EFFECT(0x04),
    VISUAL_ID(0x08),
    SOUL_CRYSTAL(0x10),
    REUSE_DELAY(0x40);

    private final int _mask;

    ItemListType(int mask) {
        _mask = mask;
    }

    @Override
    public int getMask() {
        return _mask;
    }
}
