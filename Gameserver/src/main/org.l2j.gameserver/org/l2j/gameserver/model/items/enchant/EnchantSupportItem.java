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
package org.l2j.gameserver.model.items.enchant;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.type.EtcItemType;
import org.l2j.gameserver.model.items.type.ItemType;

/**
 * @author UnAfraid
 */
public final class EnchantSupportItem extends AbstractEnchantItem {
    private final boolean _isWeapon;
    private final boolean _isBlessed;
    private final boolean _isGiant;
    private final ItemType type;

    public EnchantSupportItem(StatsSet set) {
        super(set);
        type = getItem().getItemType();
        _isWeapon = (type == EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_WP) || (type == EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_WP) || (type == EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP) || (type == EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP);
        _isBlessed = (type == EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_AM) || (type == EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_WP) || (type == EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM) || (type == EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP);
        _isGiant = (type == EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM) || (type == EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP) || (type == EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM) || (type == EtcItemType.BLESSED_GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP);
    }

    @Override
    public boolean isWeapon() {
        return _isWeapon;
    }

    public boolean isBlessed() {
        return _isBlessed;
    }

    public boolean isGiant() {
        return _isGiant;
    }
}
